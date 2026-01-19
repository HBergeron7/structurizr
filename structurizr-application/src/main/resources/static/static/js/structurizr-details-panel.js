structurizr.ui.DetailsPanel = function() {

    var detailsPanel = $('#detailsPanel');
    var detailsPanelHeader = $('#detailsPanelHeader');
    var detailsPanelName = $('#detailsPanelName');
    var detailsPanelParent = $('#detailsPanelParent');
    const detailsPanelHr = $('#detailsPanel hr');
    var detailsPanelDescription = $('#detailsPanelDescription');
    var detailsPanelMetadata = $('#detailsPanelMetadata');
    var detailsPanelTags = $('#detailsPanelTags');
    var detailsPanelProperties = $('#detailsPanelProperties');
    var detailsPanelUrl = $('#detailsPanelUrl');
    var detailsPanelAdditionalContent = $('#detailsPanelAdditionalContent');

    this.showDetailsForElement = function(element, style, perspective) {
        if (element === undefined) {
            return;
        }

        detailsPanelName.html(structurizr.util.escapeHtml(element.name));
        detailsPanelMetadata.text(structurizr.ui.getMetadataForElement(element, true));

        if (element.parentId) {
            var parentElement = structurizr.workspace.findElementById(element.parentId);
            detailsPanelParent.text('from ' + parentElement.name + ' [' + structurizr.workspace.getTerminologyFor(parentElement) + ']');
        } else {
            detailsPanelParent.text('');
        }

        detailsPanelDescription.html(element.description ? structurizr.util.escapeHtml(element.description).replaceAll('\n', '<br />') : '');

        detailsPanelHeader.removeClass('hidden');

        if (perspective === undefined) {
            var tagsHtml = '';
            var tags = structurizr.workspace.getAllTagsForElement(element);
            tagsHtml += '<div class="smaller">';
            tags.forEach(function (tag) {
                if (tag !== undefined) {
                    tag = tag.trim();
                    if (tag.length > 0) {
                        tagsHtml += '<span class="tag">';
                        tagsHtml += structurizr.util.escapeHtml(tag);
                        tagsHtml += '</span>';
                    }
                }
            });
            tagsHtml += '</div>';
            detailsPanelTags.html(tagsHtml);

            renderProperties(structurizr.workspace.getAllPropertiesForElement(element));

            var urlHtml = '';
            var url = element.url;
            if (url && url.trim().length > 0) {
                urlHtml += '<div class="smaller">';
                urlHtml += '<p>URL: ';
                urlHtml += '<a href="' + structurizr.util.escapeHtml(url) + '" target="_blank">' + structurizr.util.escapeHtml(url) + '</a>';
                urlHtml += '</p>';
                urlHtml += '</div>';
            }
            detailsPanelUrl.html(urlHtml);
            detailsPanelAdditionalContent.html('');
        } else {
            detailsPanelTags.html('');
            detailsPanelUrl.html('');
            detailsPanelProperties.html('');

            var perspectiveDetails = undefined;

            if (element.perspectives) {
                element.perspectives.forEach(function(p) {
                    if (p.name === perspective) {
                        perspectiveDetails = p;
                    }
                });
            }

            if (perspectiveDetails === undefined) {
                if (element.type === 'SoftwareSystemInstance') {
                    var softwareSystem = structurizr.workspace.findElementById(element.softwareSystemId);
                    if (softwareSystem.perspectives) {
                        softwareSystem.perspectives.forEach(function(p) {
                            if (p.name === perspective) {
                                perspectiveDetails = p;
                            }
                        });
                    }
                } else if (element.type === 'ContainerInstance') {
                    var container = structurizr.workspace.findElementById(element.containerId);
                    if (container.perspectives) {
                        container.perspectives.forEach(function(p) {
                            if (p.name === perspective) {
                                perspectiveDetails = p;
                            }
                        });
                    }
                }
            }

            if (perspectiveDetails !== undefined) {
                var perspectiveDescription = perspectiveDetails.description;
                if (perspectiveDescription === undefined) {
                    perspectiveDescription = '';
                }
                perspectiveDescription = structurizr.util.escapeHtml(perspectiveDescription).replaceAll('\n', '<br />');

                additionalContent = '';
                additionalContent += '<p><b>Perspective: ';
                additionalContent += structurizr.util.escapeHtml(perspectiveDetails.name);
                additionalContent += '</b></p>';
                additionalContent += '<p>';
                additionalContent += perspectiveDescription;
                additionalContent += '</p>';
            }

            detailsPanelAdditionalContent.html(additionalContent);
        }

        if (style) {
            detailsPanel.css("background", style.background);
            detailsPanel.css("border-color", style.stroke);
            detailsPanel.css("color", style.color);
            if (style.borderStyle === 'Dashed') {
                detailsPanel.css('border-style', 'dashed');
            } else if (style.borderStyle === 'Dotted') {
                detailsPanel.css('border-style', 'dotted');
            } else {
                detailsPanel.css('border-style', 'solid');
            }

            $('#detailsPanel .tag').css("border-color", style.color);
            $('#detailsPanel a').css("color", style.color);
            $('#detailsPanel a').css("text-decoration", "underline");
            $('#detailsPanel hr').css("border-color", style.stroke);
        } else {
            detailsPanel.css("background", '#ffffff');
            detailsPanel.css("border-color", '#000000');
            detailsPanel.css("color", '#000000');
            detailsPanel.css('border-style', 'solid');
            $('#detailsPanel .tag').css("border-color", '#000000');
            $('#detailsPanel a').css("color", '#000000');
            $('#detailsPanel a').css("text-decoration", "underline");
            $('#detailsPanel hr').css("border-color", '#000000');
        }
    };

    this.showDetailsForRelationship = function(relationship, relationshipInView, style, perspective) {
        if (relationship === undefined) {
            return;
        }

        if (relationshipInView === undefined) {
            relationshipInView = {};
        }

        const darkMode = structurizr.ui.isDarkMode();
        detailsPanel.css("background", (darkMode === true ? structurizr.ui.DARK_MODE_DEFAULTS.background : structurizr.ui.LIGHT_MODE_DEFAULTS.background));

        var relationshipSummary = relationshipInView.description;
        if (relationshipSummary === undefined) {
            relationshipSummary = relationship.description;
        }
        if (relationshipSummary === undefined || relationshipSummary.length === 0) {
            relationshipSummary = '';
        }

        detailsPanelName.text((relationshipInView.order ? relationshipInView.order + ': ' : '') + relationshipSummary);
        detailsPanelParent.html('');
        detailsPanelMetadata.text('[' + structurizr.workspace.getTerminologyFor(relationship) + ']');

        var description = '';
        description += '<p style="font-weight: bold">';
        description += structurizr.util.escapeHtml(structurizr.workspace.findElementById(relationship.sourceId).name);
        description += ' <span style="color: gray;">--</span> ';
        description += structurizr.util.escapeHtml(relationshipSummary);
        description += ' <span style="color: gray;">-&gt;</span> ';
        description += structurizr.util.escapeHtml(structurizr.workspace.findElementById(relationship.destinationId).name);
        description += '</p>';
        detailsPanelDescription.html(description);

        detailsPanelHeader.removeClass('hidden');

        if (perspective === undefined) {
            var tagsHtml = '';
            var tags = structurizr.workspace.getAllTagsForRelationship(relationship);
            tagsHtml += '<div class="smaller">';
            tags.forEach(function (tag) {
                if (tag !== undefined) {
                    tag = tag.trim();
                    if (tag.length > 0) {
                        tagsHtml += '<span class="tag">';
                        tagsHtml += structurizr.util.escapeHtml(tag);
                        tagsHtml += '</span>';
                    }
                }
            });
            tagsHtml += '</div>';
            detailsPanelTags.html(tagsHtml);

            renderProperties(structurizr.workspace.getAllPropertiesForRelationship(relationship));

            var urlHtml = '';
            var url = relationship.url;
            if (url && url.trim().length > 0) {
                urlHtml += '<div class="smaller">';
                urlHtml += '<p>URL: ';
                urlHtml += '<a href="' + structurizr.util.escapeHtml(url) + '" target="_blank">' + structurizr.util.escapeHtml(url) + '</a>';
                urlHtml += '</p>';
                urlHtml += '</div>';
            }
            detailsPanelUrl.html(urlHtml);
            detailsPanelAdditionalContent.html('');
        } else {
            var additionalContent = '';
            if (perspective !== undefined) {
                var perspectiveDetails = undefined;

                if (relationship.perspectives) {
                    relationship.perspectives.forEach(function(p) {
                        if (p.name === perspective) {
                            perspectiveDetails = p;
                        }
                    });
                }

                if (perspectiveDetails === undefined) {
                    if (relationship.linkedRelationshipId) {
                        var linkedRelationship = structurizr.workspace.findRelationshipById(relationship.linkedRelationshipId);
                        if (linkedRelationship && linkedRelationship.perspectives) {
                            linkedRelationship.perspectives.forEach(function(p) {
                                if (p.name === perspective) {
                                    perspectiveDetails = p;
                                }
                            });
                        }
                    }
                }

                if (perspectiveDetails !== undefined) {
                    var perspectiveDescription = perspectiveDetails.description;
                    if (perspectiveDescription === undefined) {
                        perspectiveDescription = '';
                    }
                    perspectiveDescription = structurizr.util.escapeHtml(perspectiveDescription).replaceAll('\n', '<br />');

                    additionalContent += '<p><b>Perspective: ';
                    additionalContent += structurizr.util.escapeHtml(perspectiveDetails.name);
                    additionalContent += '</b></p>';
                    additionalContent += '<p>';
                    additionalContent += perspectiveDescription;
                    additionalContent += '</p>';
                }
            }

            detailsPanelAdditionalContent.html(additionalContent);
        }

        var color = style.color;
        if (color === undefined) {
            color = (darkMode === true ? structurizr.ui.DARK_MODE_DEFAULTS.color : structurizr.io.LIGHT_MODE_DEFAULTS.color);
        }

        detailsPanel.css("border-color", color);
        detailsPanel.css("color", color);
        $('#detailsPanel .tag').css("border-color", color);
        $('#detailsPanel a').css("color", color);
        $('#detailsPanel a').css("text-decoration", "underline");
        $('#detailsPanel hr').css("border-color", color);

        detailsPanel.css('border-style', style.lineStyle);
    }

    function renderProperties(properties) {
        var propertiesHtml = '';
        if (Object.keys(properties).length > 0) {
            Object.keys(properties).forEach(function (key) {
                if (key.indexOf('structurizr.') === 0) {
                    // ignore 'structurizr.' properties
                } else {
                    var value = properties[key];
                    if (isUrl(value)) {
                        value = '<a href="' + structurizr.util.escapeHtml(value) + '" target="_blank">' + structurizr.util.escapeHtml(value) + '</a>';
                    } else {
                        value = structurizr.util.escapeHtml(value);
                    }
                    propertiesHtml += '<li>';
                    propertiesHtml += (structurizr.util.escapeHtml(key) + ' = ' + value);
                    propertiesHtml += '</li>';
                }
            });
        }

        if (propertiesHtml.length > 0) {
            detailsPanelProperties.html('<div class="smaller"><p>Properties:</p><ul>' + propertiesHtml + '</ul></div>');
        } else {
            detailsPanelProperties.html('');
        }
    }

    function isUrl(s) {
        return s !== undefined && (s.indexOf('https://') === 0 || s.indexOf('http://') === 0);
    }

};

const detailsPanel = new structurizr.ui.DetailsPanel();

