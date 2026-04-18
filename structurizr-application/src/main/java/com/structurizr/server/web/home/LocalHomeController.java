package com.structurizr.server.web.home;

import com.structurizr.configuration.Configuration;
import com.structurizr.server.domain.WorkspaceMetadata;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@org.springframework.context.annotation.Profile("command-local")
final class LocalHomeController extends AbstractHomeController {

    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    String showHomePage(
            @RequestParam(required = false, defaultValue = SORT_NAME) String sort,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_FOLDER) String folder,
            ModelMap model) {

        if (Configuration.getInstance().isSingleWorkspace()) {
            return "redirect:/workspace/1";
        } else {
            List<WorkspaceMetadata> workspaces = workspaceComponent.getWorkspaces();
            List<String> folders = collectFolders(workspaces);

            folder = determineFolder(folder);
            sort = determineSort(sort);
            workspaces = filterByFolder(workspaces, folder);
            workspaces = sortAndPaginate(new ArrayList<>(workspaces), sort, pageNumber, pageSize, model);

            model.addAttribute("workspaces", workspaces);
            model.addAttribute("numberOfWorkspaces", workspaces.size());
            model.addAttribute("folders", folder == null ? folders : List.of());
            model.addAttribute("selectedFolder", folder);

            model.addAttribute("sort", sort);
            addCommonAttributes(model, "", true);

            return "home";
        }
    }

}
