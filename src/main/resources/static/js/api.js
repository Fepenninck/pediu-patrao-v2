window.chamarApi = async function (url, metodo = 'GET', dados) {
    const headers = {
        Accept: 'application/json'
    };

    if (dados !== undefined) {
        headers['Content-Type'] = 'application/json';
    }

    if (!['GET', 'HEAD', 'OPTIONS'].includes(metodo)) {
        const token = document.querySelector('meta[name="_csrf"]');
        const nomeHeader =
            document.querySelector('meta[name="_csrf_header"]');

        if (!token || !nomeHeader) {
            throw new Error('Token de segurança não encontrado. Recarregue a página.');
        }

        headers[nomeHeader.content] = token.content;
    }

    const resposta = await fetch(url, {
        method: metodo,
        headers,
        credentials: 'same-origin',
        body: dados === undefined ? undefined : JSON.stringify(dados)
    });

    if (resposta.redirected) {
        window.location.assign(resposta.url);
        throw new Error('Sua sessão precisa de autenticação.');
    }

    if (resposta.status === 204) {
        return null;
    }

    const tipo = resposta.headers.get('content-type') || '';
    const conteudo = tipo.includes('application/json')
        ? await resposta.json()
        : null;

    if (!resposta.ok) {
        const mensagem = resposta.status === 403
            ? 'Acesso negado ou token de segurança inválido.'
            : conteudo?.erro || `Erro HTTP ${resposta.status}.`;

        throw new Error(mensagem);
    }

    return conteudo;
};