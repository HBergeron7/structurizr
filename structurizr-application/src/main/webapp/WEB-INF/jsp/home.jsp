<%@ include file="/WEB-INF/fragments/quick-navigation.jspf" %>

<style>
    .workspaceSummary {
        position: relative;
        display: inline-block;
        margin: 10px;
        font-size: 18px;
        max-width: 320px;
        min-height: 320px;
        max-height: 320px;
        height: 320px;
        padding: 10px;
        border:1px solid #ddd;
        border-radius: 4px;
        overflow-y: hidden;
    }

    .workspaceThumbnail {
        display: inline-block;
        width: 300px;
        height: 200px;
        min-height: 200px;
        max-height: 200px;
        overflow-y: hidden;
        margin-top: 10px;
    }

    .folderIcon {
        margin-top: 35px;
        width: 96px;
        height: 96px;
    }
</style>

<div id="dashboard" class="section" style="padding-bottom: 0px">
    <div class="centered">

        <div style="margin-bottom: 20px">
            <c:if test="${numberOfWorkspaces > 0}">
            <c:url var="sortByNameUrl" value="/">
                <c:param name="sort" value="name" />
                <c:param name="pageSize" value="${pageSize}" />
                <c:if test="${not empty selectedFolder}">
                    <c:param name="folder" value="${selectedFolder}" />
                </c:if>
            </c:url>
            <c:url var="sortByDateUrl" value="/">
                <c:param name="sort" value="date" />
                <c:param name="pageSize" value="${pageSize}" />
                <c:if test="${not empty selectedFolder}">
                    <c:param name="folder" value="${selectedFolder}" />
                </c:if>
            </c:url>
            <a href="${sortByNameUrl}" style="color: #444444;<c:if test="${sort eq 'name'}"> font-weight: bold;</c:if>">Name</a>
            <img src="/static/bootstrap-icons/sort-down.svg" class="icon-sm" />
            <a href="${sortByDateUrl}" style="color: #444444;<c:if test="${sort eq 'date'}"> font-weight: bold;</c:if>">Date</a>
            </c:if>

            <c:if test="${not empty pageNumber}">
                <span style="padding-left: 20px; padding-right: 20px">|</span>
                <%@ include file="/WEB-INF/fragments/workspaces-page-control.jspf" %>
            </c:if>
        </div>

        <c:if test="${not empty selectedFolder}">
            <div style="margin-bottom: 20px; font-size: 24px">
                <img src="/static/bootstrap-icons/folder.svg" class="icon-md" />
                <c:out value="${selectedFolder}" escapeXml="true" />
            </div>
        </c:if>

        <c:forEach var="folder" items="${folders}">
            <c:url var="folderUrl" value="/">
                <c:param name="folder" value="${folder}" />
                <c:param name="sort" value="${sort}" />
                <c:param name="pageNumber" value="1" />
                <c:param name="pageSize" value="${pageSize}" />
            </c:url>
            <div class="workspaceSummary centered">
                <div>
                    <a href="${folderUrl}"><c:out value="${folder}" escapeXml="true" /></a>
                </div>

                <div class="workspaceThumbnail" style="padding-top: 20px; margin-bottom: 10px">
                    <a href="${folderUrl}"><img src="/static/bootstrap-icons/folder.svg" class="folderIcon" /></a>
                </div>
            </div>
        </c:forEach>

        <c:forEach var="workspace" items="${workspaces}" varStatus="status">
            <div class="workspaceSummary centered <c:if test="${not workspace.active}">inactive</c:if>">
                <div>
                    <a href="${workspace.urlPrefix}/${workspace.id}"><c:out value="${workspace.name}" escapeXml="true" /></a>
                </div>

                <div style="margin-top: 10px; margin-bottom: 10px; font-size: 11px">
                    <c:out value="${workspace.description}" escapeXml="true" />
                </div>

                <div class="workspaceThumbnail">
                    <a href="${workspace.urlPrefix}/${workspace.id}">
                    <img src="${workspace.urlPrefix}/${workspace.id}/images/thumbnail.png" alt="Thumbnail" class="img-light img-fluid workspaceThumbnailImage" />
                    <img src="${workspace.urlPrefix}/${workspace.id}/images/thumbnail-dark.png" alt="Thumbnail" class="img-dark img-fluid workspaceThumbnailImage" />
                    </a>
                </div>
            </div>
        </c:forEach>

        <c:if test="${userCanCreateWorkspace}">
            <div class="workspaceSummary centered">
                <div>
                    New workspace
                </div>

                <br /><br /><br />

                <div>
                    <div class="workspaceThumbnail" style="padding-top: 30px">
                        <a href="/workspace/create"><img src="/static/bootstrap-icons/folder-plus.svg" class="icon-xxl" /></a>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty pageNumber}">
        <div style="margin-top: 20px">
            <%@ include file="/WEB-INF/fragments/workspaces-page-control.jspf" %>
            <span style="padding-left: 20px; padding-right: 20px">|</span>
            Page size:
            <c:url var="pageSize10Url" value="/">
                <c:param name="sort" value="${sort}" />
                <c:param name="pageNumber" value="1" />
                <c:param name="pageSize" value="10" />
                <c:if test="${not empty selectedFolder}">
                    <c:param name="folder" value="${selectedFolder}" />
                </c:if>
            </c:url>
            <c:url var="pageSize20Url" value="/">
                <c:param name="sort" value="${sort}" />
                <c:param name="pageNumber" value="1" />
                <c:param name="pageSize" value="20" />
                <c:if test="${not empty selectedFolder}">
                    <c:param name="folder" value="${selectedFolder}" />
                </c:if>
            </c:url>
            <c:url var="pageSize50Url" value="/">
                <c:param name="sort" value="${sort}" />
                <c:param name="pageNumber" value="1" />
                <c:param name="pageSize" value="50" />
                <c:if test="${not empty selectedFolder}">
                    <c:param name="folder" value="${selectedFolder}" />
                </c:if>
            </c:url>
            <a href="${pageSize10Url}">10</a>
            |
            <a href="${pageSize20Url}">20</a>
            |
            <a href="${pageSize50Url}">50</a>
        </div>
        </c:if>
    </div>
</div>

<script nonce="${scriptNonce}">
    $('.workspaceThumbnailImage').on('error', function() {
        $(this).on('error', undefined);
        $(this).attr('src', '/static/img/thumbnail-not-available.png');
    });

    <c:forEach var="workspace" items="${workspaces}">
    quickNavigation.addItem('${workspace.id} - <c:out value="${workspace.name}" escapeXml="true" />', '${workspace.urlPrefix}/${workspace.id}');
    </c:forEach>
</script>
