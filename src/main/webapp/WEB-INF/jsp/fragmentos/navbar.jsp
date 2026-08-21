<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Usuario, dao.ImovelDAO" %>
<%--
  Navbar única usada em todas as páginas do sistema (item 3.3 da revisão
  de UX) — mesma estrutura/estilo da landing (index.jsp): logo fixa à
  esquerda, dropdowns "Comprar"/"Alugar" com lista de cidades, links
  "Catálogo"/"Financiamento", e à direita o avatar (com "Meus Imóveis"
  condicional no dropdown, item 3.2/5.5) ou o botão "Entrar".

  Parâmetro opcional de página: defina o atributo de escopo de REQUISIÇÃO
  "navFixa" como true antes do include para começar já com o fundo
  sólido (páginas internas, sem hero transparente por trás). Precisa ser
  escopo de requisição, não de página — um <jsp:include> roda a página
  incluída com seu próprio PageContext, então um atributo de escopo de
  página do include-r nunca apareceria aqui.
--%>
<%
  Usuario usuarioLogadoNav = (Usuario) session.getAttribute("usuarioLogado");
  boolean temImovelAnunciado = false;
  if (usuarioLogadoNav != null) {
    try {
      temImovelAnunciado = new ImovelDAO().contarPorUsuario(usuarioLogadoNav.getId()) > 0;
    } catch (dao.DAOException e) {
      // Falha ao consultar: não impede a navegação, só esconde o item.
    }
  }
  Object navFixaAttr = request.getAttribute("navFixa");
  boolean navFixa = navFixaAttr != null && (Boolean) navFixaAttr;
%>
<header class="nav<%= navFixa ? " is-scrolled" : "" %>"<%= navFixa ? " data-fixa=\"true\"" : "" %>>
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/index.jsp" aria-label="Habittar — início">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <div class="nav-dropdown">
        <button type="button" class="nav-dropdown__trigger">
          Comprar
          <svg class="nav-dropdown__chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
        </button>
        <div class="nav-dropdown__panel">
          <p class="nav-dropdown__titulo">Cidade</p>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=S%C3%A3o+Paulo">São Paulo</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Rio+de+Janeiro">Rio de Janeiro</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Belo+Horizonte">Belo Horizonte</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Vit%C3%B3ria">Vitória</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Curitiba">Curitiba</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Porto+Alegre">Porto Alegre</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda" class="nav-dropdown__ver-todos">Ver todos os imóveis</a>
        </div>
      </div>
      <div class="nav-dropdown">
        <button type="button" class="nav-dropdown__trigger">
          Alugar
          <svg class="nav-dropdown__chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
        </button>
        <div class="nav-dropdown__panel">
          <p class="nav-dropdown__titulo">Cidade</p>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=S%C3%A3o+Paulo">São Paulo</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Rio+de+Janeiro">Rio de Janeiro</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Belo+Horizonte">Belo Horizonte</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Vit%C3%B3ria">Vitória</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Curitiba">Curitiba</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Porto+Alegre">Porto Alegre</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel" class="nav-dropdown__ver-todos">Ver todos os imóveis</a>
        </div>
      </div>
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
      <a href="${pageContext.request.contextPath}/financiamento">Financiamento</a>
      <% if (usuarioLogadoNav != null) { %>
        <div class="avatar-menu">
          <a class="avatar-com-seta" href="${pageContext.request.contextPath}/perfil" title="Meu perfil" aria-label="Meu perfil">
            <span class="avatar">
              <% if (usuarioLogadoNav.getFotoPerfil() != null && !usuarioLogadoNav.getFotoPerfil().isBlank()) { %>
                <img src="${pageContext.request.contextPath}${sessionScope.usuarioLogado.fotoPerfil}" alt="">
              <% } else { %>
                ${sessionScope.usuarioLogado.inicial}
              <% } %>
            </span>
            <svg class="nav-dropdown__chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
          </a>
          <div class="avatar-menu__dropdown">
            <div class="avatar-menu__dropdown-inner">
              <a href="${pageContext.request.contextPath}/perfil">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 21a8 8 0 1 0-16 0"/><circle cx="12" cy="8" r="5"/></svg>
                Ver meu perfil
              </a>
              <% if (temImovelAnunciado) { %>
                <a href="${pageContext.request.contextPath}/imoveis-anunciados">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 10.5 12 4l9 6.5V20a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M9.5 21v-6h5v6"/></svg>
                  Meus imóveis
                </a>
              <% } %>
              <a href="${pageContext.request.contextPath}/logout">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="m16 17 5-5-5-5"/><path d="M21 12H9"/></svg>
                Sair
              </a>
            </div>
          </div>
        </div>
      <% } else { %>
        <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/login">Entrar</a>
      <% } %>
      <a class="btn btn--primary btn--sm btn--interactive" href="${pageContext.request.contextPath}/anunciar">
        <span class="btn__label">
          <span class="btn__pin" aria-hidden="true">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.5"/></svg>
          </span>
          Anunciar imóvel
        </span>
        <span class="btn__reveal" aria-hidden="true">
          Anunciar imóvel
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </span>
        <span class="btn__dot" aria-hidden="true"></span>
      </a>
    </nav>
  </div>
</header>
