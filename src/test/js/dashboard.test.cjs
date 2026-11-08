const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const source = fs.readFileSync('src/main/resources/static/js/dashboard.js', 'utf8');

function elemento() {
    return { value: '', hidden: false, disabled: false, dataset: { api: '/api/dashboard' },
        textContent: '', children: [], handlers: {}, attributes: {},
        addEventListener(event, fn) { this.handlers[event] = fn; },
        setAttribute(key, value) { this.attributes[key] = value; },
        reportValidity() { return true; },
        appendChild(child) { this.children.push(child); },
        append(...children) { this.children.push(...children); },
        replaceChildren(...children) { this.children = children; },
        get firstChild() { return this.children[0]; }
    };
}

async function ambiente(responder) {
    const elementos = new Map();
    const get = id => { if (!elementos.has(id)) elementos.set(id, elemento()); return elementos.get(id); };
    const requisicoes = [];
    const context = { document: { getElementById: get, createElement: elemento },
        Intl, Date, URLSearchParams, AbortController, setTimeout, clearTimeout,
        Chart: class { destroy() {} },
        fetch: async (url, options) => {
            requisicoes.push(url);
            return responder(url, options);
        }
    };
    vm.runInNewContext(source, context);
    await new Promise(resolve => setImmediate(resolve));
    return { get, requisicoes };
}

function resposta(dados, status = 200) {
    return { ok: status === 200, status, headers: { get: () => 'application/json' }, json: async () => dados };
}

function sucesso(url) {
    if (url.includes('/resumo?')) return resposta({ faturamento: 20, pedidosFinalizados: 1, ticketMedio: 20, pedidosCancelados: 0 });
    if (url.includes('/vendas-diarias?')) return resposta([{ data: '2026-11-01', faturamento: 20 }]);
    if (url.includes('/status?')) return resposta([{ status: 'FINALIZADO', quantidade: 1 }]);
    return resposta([{ produtoId: 'p1', nomeProduto: '<img src=x onerror=alert(1)>', quantidade: 2 }]);
}

test('carrega os quatro recursos e exibe produto como texto', async () => {
    const { get, requisicoes } = await ambiente(sucesso);
    assert.equal(requisicoes.length, 4);
    assert.equal(get('dashboard-resultados').hidden, false);
    assert.equal(get('ranking-produtos').children[0].children[1].textContent, '<img src=x onerror=alert(1)>');
    assert.equal(get('atualizar-dashboard').disabled, false);
});

test('período vazio apresenta mensagem e ranking vazio', async () => {
    const { get } = await ambiente(url => {
        if (url.includes('/resumo?')) return resposta({ faturamento: 0, pedidosFinalizados: 0, ticketMedio: 0, pedidosCancelados: 0 });
        return resposta([]);
    });
    assert.match(get('dashboard-mensagem').textContent, /Nenhum pedido/);
    assert.match(get('ranking-produtos').children[0].firstChild.textContent, /Nenhum produto/);
});

test('sessão expirada oculta resultados e permite tentar novamente', async () => {
    const { get } = await ambiente(() => ({ redirected: true, status: 200 }));
    assert.match(get('dashboard-mensagem').textContent, /sessão expirou/);
    assert.equal(get('dashboard-resultados').hidden, true);
    assert.equal(get('atualizar-dashboard').disabled, false);
});

test('falha da API exibe mensagem sem resultados antigos', async () => {
    const { get } = await ambiente(() => resposta({ mensagem: 'Falha na consulta' }, 500));
    assert.equal(get('dashboard-mensagem').textContent, 'Falha na consulta');
    assert.equal(get('dashboard-resultados').hidden, true);
});

test('datas invertidas não enviam nova consulta', async () => {
    const { get, requisicoes } = await ambiente(sucesso);
    get('data-inicial').value = '2026-11-07';
    get('data-final').value = '2026-11-01';
    await get('filtros-dashboard').handlers.submit({ preventDefault() {} });
    assert.equal(requisicoes.length, 4);
    assert.match(get('dashboard-mensagem').textContent, /posterior/);
    assert.equal(get('dashboard-resultados').hidden, true);
});
