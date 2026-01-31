structurizr.ui.DetailsPanel = function() {

    var detailsPanel = $('#detailsPanel');
    var detailsPanelHeader = $('#detailsPanelHeader');
    var detailsPanelName = $('#detailsPanelName');
    const detailsPanelHr = $('#detailsPanel hr');
    var detailsPanelDescription = $('#detailsPanelDescription');
    var detailsPanelDetails = $('#detailsPanelDetails');
    var detailsPanelMetadata = $('#detailsPanelMetadata');
    var detailsPanelTags = $('#detailsPanelTags');
    var detailsPanelProperties = $('#detailsPanelProperties');
    var detailsPanelUrl = $('#detailsPanelUrl');
    var detailsPanelAdditionalContent = $('#detailsPanelAdditionalContent');
    const md = window.markdownit();

    this.showDetailsForElement = function(element, style, perspective) {
        if (element === undefined) {
            return;
        }
    
        detailsPanelName.html(structurizr.util.escapeHtml(element.name));

        var description = element.description ? structurizr.util.escapeHtml(element.description).replaceAll('\n', '<br />') + '<br />' : '';
        detailsPanelDescription.html(description);

        //detailsPanelMetadata.text(structurizr.ui.getMetadataForElement(element, true));
        var metadata = {};
        metadata["Type"] = structurizr.workspace.getTerminologyFor(element); 
        if (element.parentId) {
            var parentElement = structurizr.workspace.findElementById(element.parentId);
            metadata["Parent"] = structurizr.util.escapeHtml(parentElement.name) + ' [' + structurizr.workspace.getTerminologyFor(parentElement) + ']';
        } else {
            metadata["Parent"] = "None";
        }
        metadata["Technology"] = element.technology ? structurizr.util.escapeHtml(element.technology) : 'Unknown';
        renderMetadata(metadata);
        
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

            renderDetails(element.details);
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
            detailsPanelName.css("background", style.background);
            detailsPanelName.css("border-color", style.stroke);
            detailsPanelName.css("color", style.color);
            if (style.borderStyle === 'Dashed') {
                detailsPanelName.css('border-style', 'dashed');
            } else if (style.borderStyle === 'Dotted') {
                detailsPanelName.css('border-style', 'dotted');
            } else {
                detailsPanelName.css('border-style', 'solid');
            }
        } else {
            detailsPanel.css("background", '#ffffff');
            detailsPanel.css("border-color", '#000000');
            detailsPanel.css("color", '#000000');
            detailsPanel.css('border-style', 'solid');
        }
    };

    this.showDetailsForRelationship = function(relationship, relationshipInView, style, perspective) {
        if (relationship === undefined) {
            return;
        }

        if (relationshipInView === undefined) {
            relationshipInView = {};
        }

        //const darkMode = structurizr.ui.isDarkMode();
        //detailsPanel.css("background", (darkMode === true ? structurizr.ui.DARK_MODE_DEFAULTS.background : structurizr.ui.LIGHT_MODE_DEFAULTS.background));

        var relationshipSummary = relationshipInView.description;
        if (relationshipSummary === undefined) {
            relationshipSummary = structurizr.util.escapeHtml(relationship.description);
        }
        if (relationshipSummary === undefined || relationshipSummary.length === 0) {
            relationshipSummary = '';
        }

        var sourceName = structurizr.util.escapeHtml(structurizr.workspace.findElementById(relationship.sourceId).name); 
        var destName = structurizr.util.escapeHtml(structurizr.workspace.findElementById(relationship.destinationId).name);
        detailsPanelName.html(sourceName + '<img src="/static/bootstrap-icons/arrow-right-short.svg" />' + destName);
        detailsPanelDescription.html((relationshipInView.order ? relationshipInView.order + ': ' : '') + relationshipSummary);

        var metadata = {};
        metadata["Type"] = structurizr.workspace.getTerminologyFor(relationship); 
        if (relationship.linkedRelationshipId) {
            var linkedRel = structurizr.workspace.findRelationshipById(relationship.linkedRelationshipId);
            var sourceName = structurizr.util.escapeHtml(structurizr.workspace.findElementById(linkedRel.sourceId).name); 
            var destName = structurizr.util.escapeHtml(structurizr.workspace.findElementById(linkedRel.destinationId).name);

            metadata["Implied By"] = sourceName + '<img src="/static/bootstrap-icons/arrow-right-short.svg" />' + destName;
        } 
        metadata["Technology"] = relationship.technology ? structurizr.util.escapeHtml(relationship.technology) : 'Unknown';
        renderMetadata(metadata);

        detailsPanelDetails.html('');

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

    function renderMetadata(metadata) {
        var metadataHtml = '';
        if (metadata !== undefined && Object.keys(metadata).length > 0) {
            Object.keys(metadata).forEach(function (key) {
                var value = metadata[key];

                //key and value should already be HTML escaped
                metadataHtml += '<tr>'
                metadataHtml += '<td style="width: auto; opacity: 65%;">' + key + '</td>';
                metadataHtml += '<td style="width: 100%; padding-left: 1rem;">' + value + '</td>';
                metadataHtml += '</tr>'
            });
        }

        if (metadataHtml.length > 0) {
            detailsPanelMetadata.html('<table style="width:100%;">' + metadataHtml + '</table>');
        } else {
            detailsPanelMetadata.html('');
        }
    }

    function renderDetails(details) {
        var detailsHtml = '';
        var count = 0;
        if (details !== undefined && Object.keys(details).length > 0) {
            Object.keys(details).forEach(function (key) {
                count++;
                var value = details[key];
                value = md.render(value);

                detailsHtml += '<div class="accordion-item">';
                detailsHtml += '<h2 class="accordion-header">';
                detailsHtml += '<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapse' + count + '" aria-expanded="false" aria-controls="flush-collapse' + count + '">';
                detailsHtml += structurizr.util.escapeHtml(key);
                detailsHtml += '</button>';
                detailsHtml += '</h2>';
                detailsHtml += '<div id="flush-collapse' + count + '" class="accordion-collapse collapse show">';
                detailsHtml += '<div class="accordion-body">' + value + '</div>'
                detailsHtml += '</div>'
                detailsHtml += '</div>'
            });
        }

        if (detailsHtml.length > 0) {
            detailsPanelDetails.html('<div class="accordion accordion-flush" id="accordionDetails">' + detailsHtml + '</div>');
        } else {
            detailsPanelDetails.html('');
        }
    }

    function isUrl(s) {
        return s !== undefined && (s.indexOf('https://') === 0 || s.indexOf('http://') === 0);
    }

};

const detailsPanel = new structurizr.ui.DetailsPanel();

