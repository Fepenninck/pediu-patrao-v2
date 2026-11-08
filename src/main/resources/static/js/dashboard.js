(() => {
    'use strict';
    const root = document.getElementById('dashboard');
    if (!root) return;
    const byId = id => document.getElementById(id);
    const form = byId('filtros-dashboard');
    const inicio = byId('data-inicial');
    const fim = byId('data-final');
    const resultados = byId('dashboard-resultados');
    const mensagem = byId('dashboard-mensagem');
    const botao = byId('atualizar-dashboard');
    const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
    const numero = new Intl.NumberFormat('pt-BR');
    const nomes = { RECEBIDO: 'Recebido', EM_PREPARACAO: 'Em preparação', PRONTO: 'Pronto',
        SAIU_PARA_ENTREGA: 'Saiu para entrega', RETIRADO: 'Retirado', FINALIZADO: 'Finalizado', CANCELADO: 'Cancelado' };
    let graficoVendas;
    let graficoStatus;

    function dataLocal(data) {
        return `${data.getFullYear()}-${String(data.getMonth() + 1).padStart(2, '0')}-${String(data.getDate()).padStart(2, '0')}`;
    }

    function periodo(dias) {
        const hoje = new Date();
        const primeiro = new Date(hoje);
        primeiro.setDate(hoje.getDate() - dias + 1);
        inicio.value = dataLocal(primeiro);
        fim.value = dataLocal(hoje);
    }

    function linha(valores) {
        const tr = document.createElement('tr');
        valores.forEach(valor => {
            const td = document.createElement('td');
            td.textContent = valor;
            tr.appendChild(td);
        });
        return tr;
    }

    async function consultar(recurso, parametros, signal) {
        const resposta = await fetch(`${root.dataset.api}/${recurso}?${parametros}`, {
            signal, headers: { Accept: 'application/json' }, credentials: 'same-origin', cache: 'no-store'
        });
        if (resposta.redirected || resposta.status === 401) {
            throw new Error('Sua sessão expirou. Entre no sistema novamente.');
        }
        if (resposta.status === 403) throw new Error('Seu perfil não tem permissão para consultar o dashboard.');
        if (!resposta.headers.get('content-type')?.includes('application/json')) {
            throw new Error('O servidor retornou uma resposta inesperada. Entre novamente ou tente mais tarde.');
        }
        const dados = await resposta.json();
        if (!resposta.ok) throw new Error(dados.mensagem || 'Não foi possível consultar os dados.');
        return dados;
    }

    function renderizar(resumo, vendas, status, produtos) {
        byId('indicador-faturamento').textContent = moeda.format(resumo.faturamento);
        byId('indicador-finalizados').textContent = numero.format(resumo.pedidosFinalizados);
        byId('indicador-ticket').textContent = moeda.format(resumo.ticketMedio);
        byId('indicador-cancelados').textContent = numero.format(resumo.pedidosCancelados);
        const datas = vendas.map(v => v.data.split('-').reverse().join('/'));
        byId('vendas-tabela').replaceChildren(...vendas.map((v, i) => linha([datas[i], moeda.format(v.faturamento)])));
        byId('status-lista').replaceChildren(...status.map(s => {
            const li = document.createElement('li');
            const nome = document.createElement('span'); nome.textContent = nomes[s.status] || s.status;
            const quantidade = document.createElement('strong'); quantidade.textContent = numero.format(s.quantidade);
            li.append(nome, quantidade);
            return li;
        }));
        const ranking = byId('ranking-produtos');
        ranking.replaceChildren(...produtos.map((p, i) => linha([i + 1, p.nomeProduto || 'Produto sem nome', numero.format(p.quantidade)])));
        if (!produtos.length) {
            const tr = linha(['Nenhum produto vendido no período.']);
            tr.firstChild.colSpan = 3;
            ranking.appendChild(tr);
        }
        graficoVendas?.destroy();
        graficoStatus?.destroy();
        if (typeof Chart === 'undefined') throw new Error('Não foi possível carregar os gráficos. Recarregue a página.');
        graficoVendas = new Chart(byId('grafico-vendas'), {
            type: 'bar',
            data: { labels: datas, datasets: [{ label: 'Faturamento', data: vendas.map(v => Number(v.faturamento)), backgroundColor: '#b95c2b' }] },
            options: { responsive: true, maintainAspectRatio: false, legend: { display: false },
                scales: { yAxes: [{ ticks: { beginAtZero: true, callback: valor => moeda.format(valor) } }], xAxes: [{ ticks: { maxTicksLimit: 10 } }] },
                tooltips: { callbacks: { label: item => moeda.format(item.yLabel) } } }
        });
        graficoStatus = new Chart(byId('grafico-status'), {
            type: 'doughnut',
            data: { labels: status.map(s => nomes[s.status] || s.status), datasets: [{ data: status.map(s => s.quantidade),
                backgroundColor: ['#718096', '#e9a23b', '#4d9a9a', '#6876b4', '#aa7aab', '#398359', '#c45454'] }] },
            options: { responsive: true, maintainAspectRatio: false, legend: { display: false } }
        });
        return status.reduce((total, s) => total + s.quantidade, 0);
    }

    async function atualizar(event) {
        event?.preventDefault();
        if (botao.disabled || !form.reportValidity()) return;
        resultados.hidden = true;
        mensagem.className = 'alert alert-info';
        if (inicio.value > fim.value) {
            mensagem.className = 'alert alert-danger';
            mensagem.textContent = 'A data inicial não pode ser posterior à data final.';
            return;
        }
        botao.disabled = true;
        root.setAttribute('aria-busy', 'true');
        mensagem.textContent = 'Consultando dados…';
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 20000);
        try {
            const parametros = new URLSearchParams({ dataInicial: inicio.value, dataFinal: fim.value });
            const dados = await Promise.all(['resumo', 'vendas-diarias', 'status', 'produtos-mais-vendidos']
                .map(recurso => consultar(recurso, parametros, controller.signal)));
            // O canvas precisa estar visível para o Chart.js medir sua largura.
            resultados.hidden = false;
            const total = renderizar(...dados);
            mensagem.className = total ? 'small text-muted' : 'alert alert-secondary';
            mensagem.textContent = total ? `${numero.format(total)} pedidos no período consultado.` : 'Nenhum pedido encontrado no período selecionado.';
            byId('ultima-atualizacao').textContent = `Atualizado às ${new Date().toLocaleTimeString('pt-BR')}`;
        } catch (erro) {
            controller.abort();
            resultados.hidden = true;
            mensagem.className = 'alert alert-danger';
            mensagem.textContent = erro.name === 'AbortError' ? 'A consulta demorou demais. Tente atualizar novamente.' : erro.message;
            byId('ultima-atualizacao').textContent = 'Consulta não concluída';
        } finally {
            clearTimeout(timeout);
            botao.disabled = false;
            root.setAttribute('aria-busy', 'false');
        }
    }

    byId('periodo-rapido').addEventListener('change', event => {
        if (event.target.value !== 'custom') { periodo(Number(event.target.value)); atualizar(); }
    });
    [inicio, fim].forEach(input => input.addEventListener('change', () => { byId('periodo-rapido').value = 'custom'; }));
    form.addEventListener('submit', atualizar);
    periodo(7);
    atualizar();
})();
