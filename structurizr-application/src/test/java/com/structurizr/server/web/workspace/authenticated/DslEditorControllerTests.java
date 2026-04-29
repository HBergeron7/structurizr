package com.structurizr.server.web.workspace.authenticated;

import com.structurizr.Workspace;
import com.structurizr.configuration.Configuration;
import com.structurizr.configuration.Features;
import com.structurizr.dsl.DslUtils;
import com.structurizr.server.component.workspace.WorkspaceComponentException;
import com.structurizr.server.domain.WorkspaceMetadata;
import com.structurizr.server.web.AbstractTestsBase;
import com.structurizr.server.web.MockWorkspaceComponent;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.SystemLandscapeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;

import static org.junit.jupiter.api.Assertions.*;

public class DslEditorControllerTests extends AbstractTestsBase {

    private DslEditorController controller;
    private ModelMap model;

    @BeforeEach
    public void setUp() {
        controller = new DslEditorController();
        model = new ModelMap();
    }

    @Test
    void showAuthenticatedDslEditor_ReturnsAnErrorPage_WhenTheDslEditorHasBeenDisabled() {
        configureAsServerWithAuthenticationEnabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);
        setUser("user@example.com");

        Configuration.getInstance().setFeatureDisabled(Features.UI_DSL_EDITOR);
        String view = controller.showAuthenticatedDslEditor(1, "", "version", model);
        assertEquals("dsl-editor-disabled", view);
    }

    @Test
    void showAuthenticatedDslEditor_ReturnsThe404Page_WhenTheWorkspaceDoesNotExist() {
        configureAsServerWithAuthenticationEnabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);
        setUser("user@example.com");

        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public WorkspaceMetadata getWorkspaceMetadata(long workspaceId) {
                return null;
            }
        });

        String view = controller.showAuthenticatedDslEditor(1, "", "version", model);
        assertEquals("404", view);
    }

    @Test
    void showAuthenticatedDslEditor_ReturnsAnErrorPage_WhenTheWorkspaceIsClientSideEncrypted() {
        configureAsServerWithAuthenticationEnabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);
        setUser("user@example.com");

        final WorkspaceMetadata workspaceMetaData = new WorkspaceMetadata(1);
        workspaceMetaData.setClientSideEncrypted(true);
        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public WorkspaceMetadata getWorkspaceMetadata(long workspaceId) {
                return workspaceMetaData;
            }
        });

        String view = controller.showAuthenticatedDslEditor(1, "", "version", model);
        assertEquals("workspace-is-client-side-encrypted", view);
    }

    @Test
    void showAuthenticatedDslEditor_ReturnsThe404Page_WhenTheUserDoesNotHaveAccess() {
        configureAsServerWithAuthenticationEnabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);
        setUser("user@example.com");

        final WorkspaceMetadata workspaceMetaData = new WorkspaceMetadata(1);
        workspaceMetaData.addWriteUser("user2@example.com");
        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public WorkspaceMetadata getWorkspaceMetadata(long workspaceId) {
                return workspaceMetaData;
            }
        });

        setUser("user1@example.com");
        String view = controller.showAuthenticatedDslEditor(1, "", "version", model);
        assertEquals("404", view);
    }

    @Test
    void showAuthenticatedDslEditor_ReturnsTheDslEditorPage_WhenAuthenticationIsDisabled()  {
        configureAsServerWithAuthenticationDisabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);

        final WorkspaceMetadata workspaceMetaData = new WorkspaceMetadata(1);
        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public WorkspaceMetadata getWorkspaceMetadata(long workspaceId) {
                return workspaceMetaData;
            }

            @Override
            public String getWorkspace(long workspaceId, String branch, String version) throws WorkspaceComponentException {
                return "json";
            }

            @Override
            public boolean lockWorkspace(long workspaceId, String username, String agent) {
                workspaceMetaData.addLock(username, agent);
                return true;
            }
        });

        String view = controller.showAuthenticatedDslEditor(1, "", "version", model);
        assertEquals("dsl-editor", view);
        assertSame(workspaceMetaData, model.getAttribute("workspace"));
        assertTrue(workspaceMetaData.isEditable());
        assertNull(model.getAttribute("workspaceAsJson"));
        assertEquals("/workspace/1", model.getAttribute("urlPrefix"));
        assertEquals("/workspace/1/images/", model.getAttribute("thumbnailUrl"));
        assertTrue(workspaceMetaData.isLocked());
        assertTrue(workspaceMetaData.getLockedUser().matches("[0-9]*"));
        assertTrue(workspaceMetaData.getLockedAgent().startsWith("structurizr/dsl-editor/"));
        assertEquals(true, model.getAttribute("retainWorkspaceLock"));
    }

    @Test
    void showAuthenticatedDslEditor_ReturnsTheDslEditorPage_WhenTheWorkspaceHasNoUsersConfigured()  {
        configureAsServerWithAuthenticationEnabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);
        setUser("user@example.com");

        final WorkspaceMetadata workspaceMetaData = new WorkspaceMetadata(1);
        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public WorkspaceMetadata getWorkspaceMetadata(long workspaceId) {
                return workspaceMetaData;
            }

            @Override
            public String getWorkspace(long workspaceId, String branch, String version) throws WorkspaceComponentException {
                return "json";
            }

            @Override
            public boolean lockWorkspace(long workspaceId, String username, String agent) {
                workspaceMetaData.addLock(username, agent);
                return true;
            }
        });

        String view = controller.showAuthenticatedDslEditor(1, "", "version", model);
        assertEquals("dsl-editor", view);
        assertSame(workspaceMetaData, model.getAttribute("workspace"));
        assertTrue(workspaceMetaData.isEditable());
        assertNull(model.getAttribute("workspaceAsJson"));
        assertEquals("/workspace/1", model.getAttribute("urlPrefix"));
        assertEquals("/workspace/1/images/", model.getAttribute("thumbnailUrl"));
        assertTrue(workspaceMetaData.isLocked());
        assertEquals("user@example.com", workspaceMetaData.getLockedUser());
        assertTrue(workspaceMetaData.getLockedAgent().startsWith("structurizr/dsl-editor/"));
        assertEquals(true, model.getAttribute("retainWorkspaceLock"));
    }

    @Test
    public void showAuthenticatedDslEditor_ReturnsTheDslEditorPage_WhenTheUserHasWriteAccess()  {
        configureAsServerWithAuthenticationEnabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);
        setUser("user@example.com");

        final WorkspaceMetadata workspaceMetaData = new WorkspaceMetadata(1);
        workspaceMetaData.addWriteUser("user1@example.com");
        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public WorkspaceMetadata getWorkspaceMetadata(long workspaceId) {
                return workspaceMetaData;
            }

            @Override
            public String getWorkspace(long workspaceId, String branch, String version) throws WorkspaceComponentException {
                return "json";
            }

            @Override
            public boolean lockWorkspace(long workspaceId, String username, String agent) {
                workspaceMetaData.addLock(username, agent);
                return true;
            }
        });

        setUser("user1@example.com");
        String view = controller.showAuthenticatedDslEditor(1, "", "version", model);
        assertEquals("dsl-editor", view);
        assertSame(workspaceMetaData, model.getAttribute("workspace"));
        assertTrue(workspaceMetaData.isEditable());
        assertNull(model.getAttribute("workspaceAsJson"));
        assertEquals("/workspace/1", model.getAttribute("urlPrefix"));
        assertEquals("/workspace/1/images/", model.getAttribute("thumbnailUrl"));
        assertTrue(workspaceMetaData.isLocked());
        assertEquals("user1@example.com", workspaceMetaData.getLockedUser());
        assertTrue(workspaceMetaData.getLockedAgent().startsWith("structurizr/dsl-editor/"));
        assertEquals(true, model.getAttribute("retainWorkspaceLock"));
    }

    @Test
    public void showAuthenticatedDslEditor_ReturnsAnErrorPage_WhenTheUserHasReadAccess()  {
        configureAsServerWithAuthenticationEnabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);
        setUser("user@example.com");

        final WorkspaceMetadata workspaceMetaData = new WorkspaceMetadata(1);
        workspaceMetaData.addReadUser("user1@example.com");
        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public WorkspaceMetadata getWorkspaceMetadata(long workspaceId) {
                return workspaceMetaData;
            }

            @Override
            public String getWorkspace(long workspaceId, String branch, String version) throws WorkspaceComponentException {
                return "json";
            }
        });

        setUser("user1@example.com");
        String view = controller.showAuthenticatedDslEditor(1, "", "version", model);
        assertEquals("workspace-is-readonly", view);
    }

    @Test
    void postToDslEditor_PreservesViewPropertiesUsedForSyntheticRelationshipVertices() throws Exception {
        configureAsServerWithAuthenticationEnabled();
        Configuration.getInstance().setFeatureEnabled(Features.UI_DSL_EDITOR);
        setUser("user@example.com");

        final WorkspaceMetadata workspaceMetaData = new WorkspaceMetadata(1);
        workspaceMetaData.addWriteUser("user@example.com");
        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public WorkspaceMetadata getWorkspaceMetadata(long workspaceId) {
                return workspaceMetaData;
            }
        });

        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addSoftwareSystem("Software System", "Description");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("landscape", "Description");
        view.addAllElements();
        view.addProperty("structurizr.syntheticRelationshipVertices", "{\"synthetic-1\":[{\"x\":10,\"y\":20}]}");

        DslUtils.setDsl(workspace, """
                workspace "Name" "Description" {
                    model {
                        softwareSystem "Software System" "Description"
                    }
                    views {
                        systemLandscape "landscape" "Description" {
                            include *
                        }
                    }
                }""");

        DslEditorResponse response = controller.postToDslEditor(1, WorkspaceUtils.toJson(workspace, false));

        assertTrue(response.isSuccess());

        Workspace renderedWorkspace = WorkspaceUtils.fromJson(response.getWorkspace());
        SystemLandscapeView renderedView = renderedWorkspace.getViews().getSystemLandscapeViews().iterator().next();
        assertEquals("{\"synthetic-1\":[{\"x\":10,\"y\":20}]}", renderedView.getProperties().get("structurizr.syntheticRelationshipVertices"));
    }

}
