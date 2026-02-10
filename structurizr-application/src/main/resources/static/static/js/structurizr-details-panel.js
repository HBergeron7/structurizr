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
    var detailsPanelAdditionalContent = $('#detailsPanelAdditionalContent');
    var detailsPanelInterfaces = $('#detailsPanelInterfaces');
    var detailsPanelRelationships = $('#detailsPanelRelationships');
    var detailsTabs = $('#detailsTabs');
    var tabDetails = $('#tab-details');
    var tabRelation = $('#tab-relation');
    var tabDetailsButton = $('#details-tab-button');
    var tabRelationButton = $('#relation-tab-button');
    const md = window.markdownit();

    this.showDetailsForElement = function(element, style, perspective) {
        if (element === undefined) {
            return;
        }
        detailsTabs.removeClass('hidden');
        tabDetails.addClass('active');
        tabDetails.addClass('show');
        tabRelation.removeClass('active');
        tabRelation.removeClass('show');
        tabDetailsButton.addClass('active');
        tabRelationButton.removeClass('active');
    
        detailsPanelName.html(structurizr.util.escapeHtml(element.name));

        var description = element.description ? structurizr.util.escapeHtml(element.description).replaceAll('\n', '<br />') + '<br />' : '';
        detailsPanelDescription.html(description);

        //detailsPanelMetadata.text(structurizr.ui.getMetadataForElement(element, true));
        var metadata = {};
        metadata["Type"] = structurizr.workspace.getTerminologyFor(element); 
        if (element.parentId) {
            var parentElement = structurizr.workspace.findElementById(element.parentId);
            metadata["Parent"] = structurizr.util.escapeHtml(parentElement.name);// + ' [' + structurizr.workspace.getTerminologyFor(parentElement) + ']';
        } else {
            metadata["Parent"] = "None";
        }
        metadata["Technology"] = element.technology ? structurizr.util.escapeHtml(element.technology) : 'Unknown';
        if (element.url) { 
            metadata["Url"] = '<a href="' + structurizr.util.escapeHtml(element.url) + '" target="_blank">' + structurizr.util.escapeHtml(element.url) + '</a>';
        }

        if (element.containers !== undefined && element.containers.length > 0) {
            metadata["Children"] = element.containers.length + " Containers";
            var totalProvides = [];
            var totalConsumes = [];

            element.containers.forEach(function (child) {
                totalProvides = totalProvides.concat(child.provides ? child.provides : []);
                totalConsumes = totalConsumes.concat(child.consumes ? child.consumes : []);
            });

            totalProvides = totalProvides.concat(element.provides ? element.provides : []);
            totalConsumes = totalConsumes.concat(element.consumes ? element.consumes : []);
            renderInterfaces(totalProvides, []); //totalConsumes);
             
        } else {
            renderInterfaces(element.provides, element.consumes);
        }

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

            detailsPanelAdditionalContent.html('');
        } else {
            detailsPanelTags.html('');
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

        const darkMode = structurizr.ui.isDarkMode();
        var colorMode = darkMode === true ? structurizr.ui.DARK_MODE_DEFAULTS : structurizr.ui.LIGHT_MODE_DEFAULTS;

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
            detailsPanel.css("background", colorMode.background);
            detailsPanel.css("border-color", colorMode.color);
            detailsPanel.css("color", colorMode.color);
            detailsPanel.css('border-style', 'solid');
        }
    };

    this.showDetailsForRelationship = function(relationship, relationshipInView, style, perspective) {
        if (relationship === undefined) {
            return;
        }

        detailsTabs.addClass('hidden');
        tabDetails.addClass('active');
        tabDetails.addClass('show');
        tabRelation.removeClass('active');
        tabRelation.removeClass('show');
        tabDetailsButton.addClass('active');
        tabRelationButton.removeClass('active');

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
        if (relationship.linkedRelationshipIdList !== undefined && relationship.linkedRelationshipIdList.length > 0) {
            var impliedHtml = "";

            relationship.linkedRelationshipIdList.forEach(function(id) {
                var linkedRel = structurizr.workspace.findRelationshipById(id);
                var sourceName = structurizr.util.escapeHtml(structurizr.workspace.findElementById(linkedRel.sourceId).name); 
                var destName = structurizr.util.escapeHtml(structurizr.workspace.findElementById(linkedRel.destinationId).name);
                impliedHtml += sourceName + '<img src="/static/bootstrap-icons/arrow-right-short.svg" />' + destName + '<br>';
            });

            metadata["Implied by"] = impliedHtml;
        } 
        metadata["Technology"] = relationship.technology ? structurizr.util.escapeHtml(relationship.technology) : 'Unknown';
        if (relationship.url) { 
            metadata["Url"] = '<a href="' + structurizr.util.escapeHtml(relationship.url) + '" target="_blank">' + structurizr.util.escapeHtml(relationship.url) + '</a>';
        }
        renderMetadata(metadata);

        // TODO: loop over linked relationships and cleanup 'detailedDescription'
        detailsPanelDetails.html(relationship.detailedDescription ? relationship.detailedDescription : '');

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

        const darkMode = structurizr.ui.isDarkMode();
        var colorMode = darkMode === true ? structurizr.ui.DARK_MODE_DEFAULTS : structurizr.ui.LIGHT_MODE_DEFAULTS;

        if (style) {
            detailsPanelName.css("background", colorMode.background);
            detailsPanelName.css("border-color", style.color ? style.color : colorMode.color);
            detailsPanelName.css("color", colorMode.color);
            detailsPanelName.css('border-style', style.lineStyle);
        } else {
            detailsPanel.css("background", colorMode.background);
            detailsPanel.css("border-color", colorMode.color);
            detailsPanel.css("color", colorMode.color);
            detailsPanel.css('border-style', 'solid');
        }
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
                metadataHtml += '<tr>';
                metadataHtml += '<td style="width: auto; opacity: 65%;">' + key + '</td>';
                metadataHtml += '<td style="width: 100%; padding-left: 1rem;">' + value + '</td>';
                metadataHtml += '</tr>';
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
                detailsHtml += '<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapse' + count + '" aria-expanded="false" aria-controls="flush-collapse' + count + '">';
                detailsHtml += structurizr.util.escapeHtml(key);
                detailsHtml += '</button>';
                detailsHtml += '</h2>';
                detailsHtml += '<div id="flush-collapse' + count + '" class="accordion-collapse collapse show">';
                detailsHtml += '<div class="accordion-body">' + value + '</div>';
                detailsHtml += '</div>';
                detailsHtml += '</div>';
            });
        }

        if (detailsHtml.length > 0) {
            detailsPanelDetails.html('<div class="accordion accordion-flush" id="accordionDetails">' + detailsHtml + '</div>');
        } else {
            detailsPanelDetails.html('');
        }
    }

    function renderInterfaces(provides, consumes) {
        var interfacesHtml = '';
        var providesHtml = '';
        var consumesHtml = '';

        var count = 0;
        // Add provides to interfaces
        if (provides !== undefined) {
            provides.forEach(function (p) {
                count++;
                providesHtml += '<div class="accordion-item">';
                providesHtml += '<h2 class="accordion-header">';
                providesHtml += '<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#provides' + count + '" aria-expanded="false" aria-controls="provides' + count + '">';
                providesHtml += structurizr.util.escapeHtml(p.key);
                providesHtml += '</button>';
                providesHtml += '</h2>';
                providesHtml += '<div id="provides' + count + '" class="accordion-collapse collapse">';
                providesHtml += '<div class="accordion-body">' + (p.description ? structurizr.util.escapeHtml(p.description) : '');
                providesHtml += '<table style="width:100%;">';
                providesHtml += '<tr>';
                providesHtml += '<td style="width: auto; opacity: 65%;">Action</td>';
                providesHtml += '<td style="width: 100%; padding-left: 1rem;">' + structurizr.util.escapeHtml(p.action) + '</td>';
                providesHtml += '</tr>';
                providesHtml += '<tr>';
                providesHtml += '<td style="width: auto; opacity: 65%;">Technology</td>';
                providesHtml += '<td style="width: 100%; padding-left: 1rem;">' + structurizr.util.escapeHtml(p.technology) + '</td>';
                if (p.properties !== undefined && Object.keys(p.properties).length > 0) {
                    providesHtml += '<tr>';
                    providesHtml += '<td style="width: auto; opacity: 65%;">Properties</td>';
                    providesHtml += '<td style="width: 100%; padding-left: 1rem;">';
                    Object.keys(p.properties).forEach(function (key) {
                        var value = p.properties[key];
                        providesHtml += key + '=' + value + '<br>';
                    });
                    providesHtml += '</td>';
                    providesHtml += '</tr>';
                }
                providesHtml += '</tr>';
                providesHtml += '</table>';
                providesHtml += '</div>';
                providesHtml += '</div>';
                providesHtml += '</div>';

                //properties
                //tags
            });
            if (providesHtml.length > 0) {
                interfacesHtml += '<div class="interface-header">Provides</div><div class="accordion accordion-flush" id="accordionProvides">' + providesHtml + '</div>';
            }
        }

        count = 0;
        // Add consumes to interfaces
        if (consumes !== undefined) {
            consumes.forEach(function (c) {
                console.log(c);
                count++;
                consumesHtml += '<div class="accordion-item">';
                consumesHtml += '<h2 class="accordion-header">';
                consumesHtml += '<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#consumes' + count + '" aria-expanded="false" aria-controls="consumes' + count + '">';
                consumesHtml += structurizr.util.escapeHtml(c.key);
                consumesHtml += '</button>';
                consumesHtml += '</h2>';
                consumesHtml += '<div id="consumes' + count + '" class="accordion-collapse collapse">';
                consumesHtml += '<div class="accordion-body">' + (c.description ? structurizr.util.escapeHtml(c.description) : '');
                consumesHtml += '<table style="width:100%;">';
                consumesHtml += '<tr>';
                consumesHtml += '<td style="width: auto; opacity: 65%;">Technology</td>';
                consumesHtml += '<td style="width: 100%; padding-left: 1rem;">' + (c.technology ? structurizr.util.escapeHtml(c.technology) : 'Unknown') + '</td>';
                if (c.properties !== undefined && Object.keys(c.properties).length > 0) {
                    consumesHtml += '<tr>';
                    consumesHtml += '<td style="width: auto; opacity: 65%;">Properties</td>';
                    consumesHtml += '<td style="width: 100%; padding-left: 1rem;">';
                    Object.keys(c.properties).forEach(function (key) {
                        var value = c.properties[key];
                        consumesHtml += key + '=' + value + '<br>';
                    });
                    consumesHtml += '</td>';
                    consumesHtml += '</tr>';
                }
                consumesHtml += '</tr>';
                consumesHtml += '</table>';
                consumesHtml += '</div>';
                consumesHtml += '</div>';
                consumesHtml += '</div>';

                //filters
                //tags
            });

            if (consumesHtml.length > 0) {
                interfacesHtml += '<div class="interface-header">Consumes</div><div class="accordion accordion-flush" id="accordionConsumes">' + consumesHtml + '</div>';
            }
        }

        detailsPanelInterfaces.html(interfacesHtml);
    }

    function isUrl(s) {
        return s !== undefined && (s.indexOf('https://') === 0 || s.indexOf('http://') === 0);
    }

};

const detailsPanel = new structurizr.ui.DetailsPanel();

