package com.structurizr.server.web.home;

import com.structurizr.configuration.StructurizrProperties;
import com.structurizr.server.domain.User;
import com.structurizr.server.domain.WorkspaceMetadata;
import com.structurizr.server.web.AbstractTestsBase;
import com.structurizr.server.web.MockWorkspaceComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerHomeControllerTests extends AbstractTestsBase {

    private ServerHomeController controller;
    private ModelMap model;

    @BeforeEach
    void setUp() {
        controller = new ServerHomeController();

        model = new ModelMap();
    }

    @Test
    void showHomePage_WhenAuthenticationIsDisabled() {
        configureAsServerWithAuthenticationDisabled();

        WorkspaceMetadata workspace1 = new WorkspaceMetadata(1);

        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public List<WorkspaceMetadata> getWorkspaces(User user) {
                return List.of(workspace1);
            }
        });

        String result = controller.showHomePage("", 1, 20, "", model);

        assertEquals(1, model.getAttribute("numberOfWorkspaces"));
        assertTrue(((Collection)model.getAttribute("workspaces")).contains(workspace1));
        assertEquals("home", result);
        assertEquals(true, model.getAttribute("userCanCreateWorkspace"));
    }

    @Test
    void showHomePage_ShowsFoldersAndOnlyUnfolderedWorkspaces_OnTheRootPage() {
        configureAsServerWithAuthenticationDisabled();

        WorkspaceMetadata workspace1 = new WorkspaceMetadata(1);
        workspace1.setName("Workspace 1");

        WorkspaceMetadata workspace2 = new WorkspaceMetadata(2);
        workspace2.setName("Workspace 2");
        workspace2.setFolder("Folder 1");

        WorkspaceMetadata workspace3 = new WorkspaceMetadata(3);
        workspace3.setName("Workspace 3");
        workspace3.setFolder("Folder 2");

        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public List<WorkspaceMetadata> getWorkspaces(User user) {
                return List.of(workspace1, workspace2, workspace3);
            }
        });

        String result = controller.showHomePage("", 1, 20, "", model);

        assertEquals(List.of("Folder 1", "Folder 2"), model.getAttribute("folders"));
        assertEquals(null, model.getAttribute("selectedFolder"));
        assertEquals(List.of(workspace1), model.getAttribute("workspaces"));
        assertEquals("home", result);
    }

    @Test
    void showHomePage_ShowsOnlyWorkspacesInTheSelectedFolder() {
        configureAsServerWithAuthenticationDisabled();

        WorkspaceMetadata workspace1 = new WorkspaceMetadata(1);
        workspace1.setName("Workspace 1");

        WorkspaceMetadata workspace2 = new WorkspaceMetadata(2);
        workspace2.setName("Workspace 2");
        workspace2.setFolder("Folder 1");

        WorkspaceMetadata workspace3 = new WorkspaceMetadata(3);
        workspace3.setName("Workspace 3");
        workspace3.setFolder("Folder 2");

        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public List<WorkspaceMetadata> getWorkspaces(User user) {
                return List.of(workspace1, workspace2, workspace3);
            }
        });

        String result = controller.showHomePage("", 1, 20, "Folder 1", model);

        assertEquals(List.of(), model.getAttribute("folders"));
        assertEquals("Folder 1", model.getAttribute("selectedFolder"));
        assertEquals(List.of(workspace2), model.getAttribute("workspaces"));
        assertEquals("home", result);
    }


    @Test
    void showHomePage_WhenAuthenticationIsEnabledAndTheUserIsAuthenticatedAndHasAdminPermission() {
        configureAsServerWithAuthenticationEnabled();
        setUser("user@example.com");

        WorkspaceMetadata workspace1 = new WorkspaceMetadata(1);

        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public List<WorkspaceMetadata> getWorkspaces(User user) {
                return List.of(workspace1);
            }
        });

        String result = controller.showHomePage("", 1, 20, "", model);

        assertEquals(1, model.getAttribute("numberOfWorkspaces"));
        assertTrue(((Collection)model.getAttribute("workspaces")).contains(workspace1));
        assertEquals("home", result);
        assertEquals(true, model.getAttribute("userCanCreateWorkspace"));
    }

    @Test
    void showAuthenticatedDashboard_WhenAuthenticationIsEnabledAndTheUserIsAuthenticatedAndDoesNotHaveAdminPermission() {
        Properties properties = new Properties();
        properties.setProperty(StructurizrProperties.ADMIN_USERS_AND_ROLES, "admin@example.com");
        configureAsServerWithAuthenticationEnabled(properties);
        setUser("user@example.com");

        WorkspaceMetadata workspace1 = new WorkspaceMetadata(1);

        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public List<WorkspaceMetadata> getWorkspaces(User user) {
                return List.of(workspace1);
            }
        });

        String result = controller.showHomePage("", 1, 20, "", model);

        assertEquals(1, model.getAttribute("numberOfWorkspaces"));
        assertTrue(((Collection)model.getAttribute("workspaces")).contains(workspace1));
        assertEquals("home", result);
        assertEquals(false, model.getAttribute("userCanCreateWorkspace"));
    }

    @Test
    void showAuthenticatedDashboard_WhenAuthenticationIsEnabledAndTheUserIsAuthenticatedAndTheUserIsAnAdmin() {
        Properties properties = new Properties();
        properties.setProperty(StructurizrProperties.ADMIN_USERS_AND_ROLES, "admin@example.com");
        configureAsServerWithAuthenticationEnabled(properties);
        setUser("admin@example.com");

        WorkspaceMetadata workspace1 = new WorkspaceMetadata(1);

        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public List<WorkspaceMetadata> getWorkspaces(User user) {
                return List.of(workspace1);
            }
        });

        String result = controller.showHomePage("", 1, 20, "", model);

        assertEquals(1, model.getAttribute("numberOfWorkspaces"));
        assertTrue(((Collection)model.getAttribute("workspaces")).contains(workspace1));
        assertEquals("home", result);
        assertEquals(true, model.getAttribute("userCanCreateWorkspace"));
    }

    @Test
    void showAuthenticatedDashboard_WhenAuthenticationIsEnabledAndTheUserIsNotAuthenticated() {
        configureAsServerWithAuthenticationEnabled();

        WorkspaceMetadata workspace1 = new WorkspaceMetadata(1);

        controller.setWorkspaceComponent(new MockWorkspaceComponent() {
            @Override
            public List<WorkspaceMetadata> getWorkspaces(User user) {
                return List.of(workspace1);
            }
        });

        String result = controller.showHomePage("", 1, 20, "", model);

        assertEquals(1, model.getAttribute("numberOfWorkspaces"));
        assertTrue(((Collection)model.getAttribute("workspaces")).contains(workspace1));
        assertEquals("home", result);
        assertEquals(false, model.getAttribute("userCanCreateWorkspace"));
    }

}
