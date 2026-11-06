const rotas = {
    entrada: "modulos/entrada/entrada.html",
    login: "modulos/login/login.html",
    cadastro: "modulos/cadastro/cadastro.html",
    perfil: "modulos/perfil/perfil.html",
    planos: "modulos/planos/planos.html"
};

const API_URL = "http://localhost:8080/api";

async function carregarModulo(nomeRota) {
    const caminho = rotas[nomeRota];

    if (!caminho) return;

    try {
        const resposta = await fetch(caminho);

        if (!resposta.ok) {
            throw new Error("Não foi possível carregar o módulo.");
        }

        const html = await resposta.text();

        document.getElementById("conteudo-principal").innerHTML = html;

        if (nomeRota === "entrada") configurarEntrada();
        if (nomeRota === "login") configurarLogin();
        if (nomeRota === "cadastro") configurarCadastro();
        if (nomeRota === "perfil") configurarPerfil();
        if (nomeRota === "planos") configurarPlanos();

    } catch (erro) {
        console.error(erro);

        document.getElementById("conteudo-principal").innerHTML =
            "<p>Erro ao carregar a página.</p>";
    }
}


// ENTRADA
function configurarEntrada() {
    const botaoEstudante = document.getElementById("btn-estudante");
    const botaoInstituicao = document.getElementById("btn-instituicao");

    if (botaoEstudante) {
        botaoEstudante.addEventListener("click", function () {
            navegar("login");
        });
    }

    if (botaoInstituicao) {
        botaoInstituicao.addEventListener("click", function () {
            alert("Área institucional ainda não disponível.");
        });
    }
}


// LOGIN
function configurarLogin() {
    const formulario = document.getElementById("form-login");

    if (!formulario) return;

    formulario.addEventListener("submit", async function (evento) {
        evento.preventDefault();

        const email = document.getElementById("email").value.trim();
        const senha = document.getElementById("senha").value;
        const botao = formulario.querySelector("button[type='submit']");

        botao.disabled = true;
        botao.textContent = "Entrando...";

        try {
            const resposta = await fetch(`${API_URL}/estudantes/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, senha })
            });

            const dados = await resposta.json();

            if (!resposta.ok) {
                alert(dados.erro || "Não foi possível realizar o login.");
                return;
            }

            if (!dados.token || !dados.estudante) {
                alert("O servidor não retornou os dados de autenticação esperados.");
                console.error("Resposta do login:", dados);
                return;
            }

            sessionStorage.setItem("token", dados.token);

            sessionStorage.setItem(
                "estudante",
                JSON.stringify(dados.estudante)
            );

            alert("Login realizado com sucesso!");

            navegar("perfil");

        } catch (erro) {
            console.error("Erro no login:", erro);
            alert("Não foi possível conectar ao servidor.");

        } finally {
            botao.disabled = false;
            botao.textContent = "Entrar";
        }
    });

    const linkEsqueciSenha =
        document.getElementById("link-esqueci-senha");

    if (linkEsqueciSenha) {
        linkEsqueciSenha.addEventListener("click", function (evento) {
            evento.preventDefault();
            alert("A recuperação de senha ainda não está disponível.");
        });
    }

    const linkCadastro = document.getElementById("link-cadastro");

    if (linkCadastro) {
        linkCadastro.addEventListener("click", function (evento) {
            evento.preventDefault();
            navegar("cadastro");
        });
    }
}


// CADASTRO
function configurarCadastro() {
    const formulario = document.getElementById("form-cadastro");

    if (!formulario) return;

    formulario.addEventListener("submit", async function (evento) {
        evento.preventDefault();

        const nome = document.getElementById("cadastro-nome").value.trim();
        const email = document.getElementById("cadastro-email").value.trim();
        const senha = document.getElementById("cadastro-senha").value;

        const botao = formulario.querySelector("button[type='submit']");

        botao.disabled = true;
        botao.textContent = "Cadastrando...";

        try {
            const resposta = await fetch(`${API_URL}/estudantes/cadastro`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ nome, email, senha })
            });

            const dados = await resposta.json();

            if (!resposta.ok) {
                alert(dados.erro || "Não foi possível realizar o cadastro.");
                return;
            }

            alert("Cadastro realizado com sucesso! Agora você pode entrar.");

            navegar("login");

        } catch (erro) {
            console.error("Erro no cadastro:", erro);
            alert("Não foi possível conectar ao servidor.");

        } finally {
            botao.disabled = false;
            botao.textContent = "Cadastrar";
        }
    });

    const linkVoltar = document.getElementById("link-voltar-login");

    if (linkVoltar) {
        linkVoltar.addEventListener("click", function (evento) {
            evento.preventDefault();
            navegar("login");
        });
    }
}


// PERFIL
async function configurarPerfil() {
    const token = sessionStorage.getItem("token");
    const estudanteSalvo = sessionStorage.getItem("estudante");

    if (!token || !estudanteSalvo) {
        alert("Faça login para acessar seu perfil.");
        navegar("login");
        return;
    }

    const nome = document.getElementById("perfil-nome");
    const nomeDado = document.getElementById("perfil-nome-dado");
    const emailDado = document.getElementById("perfil-email-dado");
    const instituicaoDado =
        document.getElementById("perfil-instituicao-dado");

    const botaoEditar = document.getElementById("btn-editar-perfil");
    const formulario = document.getElementById("form-editar-perfil");
    const campoNome = document.getElementById("editar-nome");
    const campoEmail = document.getElementById("editar-email");
    const botaoCancelar = document.getElementById("btn-cancelar-edicao");
    const botaoSalvar = document.getElementById("btn-salvar-perfil");

    function exibirDados(estudante) {
        nome.textContent = estudante.nome;
        nomeDado.textContent = estudante.nome;
        emailDado.textContent = estudante.email;

        instituicaoDado.textContent =
            estudante.instituicaoId
                ? `Instituição ID ${estudante.instituicaoId}`
                : "Não informada";
    }

    function abrirEdicao(estudante) {
        campoNome.value = estudante.nome;
        campoEmail.value = estudante.email;

        formulario.style.display = "block";
        botaoEditar.style.display = "none";
    }

    function fecharEdicao() {
        formulario.style.display = "none";
        botaoEditar.style.display = "inline-block";
    }

    // BUSCAR DADOS ATUALIZADOS NO BACKEND
    try {
        const resposta = await fetch(`${API_URL}/estudantes/perfil`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        const dados = await resposta.json();

        if (!resposta.ok) {
            throw new Error(
                dados.erro || "Não foi possível carregar o perfil."
            );
        }

        sessionStorage.setItem("estudante", JSON.stringify(dados));

        exibirDados(dados);

    } catch (erro) {
        console.error("Erro ao carregar perfil:", erro);
        alert(erro.message);
        return;
    }

    // ABRIR EDIÇÃO
    botaoEditar.addEventListener("click", function () {
        const estudante =
            JSON.parse(sessionStorage.getItem("estudante"));

        abrirEdicao(estudante);
    });

    // CANCELAR EDIÇÃO
    botaoCancelar.addEventListener("click", function () {
        fecharEdicao();
    });

    // SALVAR ALTERAÇÕES
    formulario.addEventListener("submit", async function (evento) {
        evento.preventDefault();

        const novoNome = campoNome.value.trim();

        if (!novoNome) {
            alert("O nome é obrigatório.");
            campoNome.focus();
            return;
        }

        if (novoNome.length > 150) {
            alert("O nome deve possuir no máximo 150 caracteres.");
            return;
        }

        const estudanteAtual =
            JSON.parse(sessionStorage.getItem("estudante"));

        botaoSalvar.disabled = true;
        botaoSalvar.textContent = "Salvando...";

        try {
            const resposta = await fetch(`${API_URL}/estudantes/perfil`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    nome: novoNome,
                    instituicaoId: estudanteAtual.instituicaoId ?? null
                })
            });

            if (!resposta.ok) {
                const texto = await resposta.text();
                let dados = {};

                try {
                    dados = JSON.parse(texto);
                } catch (erro) {
                    dados.erro = texto;
                }

                throw new Error(
                    dados.erro || "Não foi possível atualizar o perfil."
                );
            }

            // CONSULTAR NOVAMENTE O BACKEND PARA CONFIRMAR
            const respostaPerfil = await fetch(
                `${API_URL}/estudantes/perfil`,
                {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                }
            );

            const estudanteAtualizado = await respostaPerfil.json();

            if (!respostaPerfil.ok) {
                throw new Error(
                    "Alteração enviada, mas não foi possível confirmar os dados."
                );
            }

            sessionStorage.setItem(
                "estudante",
                JSON.stringify(estudanteAtualizado)
            );

            exibirDados(estudanteAtualizado);
            fecharEdicao();

            alert("Perfil atualizado com sucesso!");

        } catch (erro) {
            console.error("Erro ao atualizar perfil:", erro);
            alert(erro.message);

        } finally {
            botaoSalvar.disabled = false;
            botaoSalvar.textContent = "Salvar alterações";
        }
    });
}

// PLANOS
// PLANOS
async function configurarPlanos() {

    const botaoAssinar =
        document.getElementById("btn-assinar-premium");

    // Buscar planos cadastrados no banco de dados
    try {

        const resposta = await fetch(`${API_URL}/planos`);

        if (!resposta.ok) {
            throw new Error("Erro ao carregar os planos.");
        }

        const planos = await resposta.json();

        const planoGratuito = planos.find(
            plano => plano.nome.toLowerCase() === "gratuito"
        );

        if (planoGratuito) {

            const cardGratuito =
                document.querySelector(".planos-opcoes .plano");

            cardGratuito.querySelector("h2").textContent =
                "PLANO " + planoGratuito.nome.toUpperCase();

            cardGratuito.querySelectorAll("p")[1].textContent =
                "IA: " + planoGratuito.limiteUsoIaMensal +
                " utilizações/mês";
        }

    } catch (erro) {

        console.error("Erro ao carregar planos:", erro);

    }

    // Botão Premium
    if (botaoAssinar) {
        botaoAssinar.addEventListener("click", function () {
            alert("Plano Premium selecionado!");
        });
    }
}

// NAVEGAÇÃO
function navegar(nomeRota) {
    window.location.hash = nomeRota;
    carregarModulo(nomeRota);
}


// ROTA INICIAL
const rotaAtual =
    window.location.hash.replace("#", "") || "entrada";

carregarModulo(rotaAtual);