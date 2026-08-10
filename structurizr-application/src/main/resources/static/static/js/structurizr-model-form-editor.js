var selectedModelElementPath;
var collapsedModelElementPaths = {};

    function showModelFormEditor() {
        $('#dslTextEditorTab').removeClass('active');
        $('#modelFormEditorTab').addClass('active');
        $('#sourceTextArea').addClass('hidden');
        $('#modelFormEditor').removeClass('hidden');
        renderModelFormEditor();
        resize();
    }

    function showDslTextEditor() {
        $('#modelFormEditorTab').removeClass('active');
        $('#dslTextEditorTab').addClass('active');
        $('#modelFormEditor').addClass('hidden');
        $('#sourceTextArea').removeClass('hidden');
        editor.resize(true);
    }

    function tokenizeDsl(source) {
        var tokens = [];
        var index = 0;

        while (index < source.length) {
            var character = source.charAt(index);
            if (/\s/.test(character)) {
                index++;
            } else if (source.substr(index, 2) === '//') {
                index = source.indexOf('\n', index);
                if (index === -1) {
                    index = source.length;
                }
            } else if (source.substr(index, 2) === '/*') {
                index = source.indexOf('*/', index + 2);
                index = index === -1 ? source.length : index + 2;
            } else if (source.substr(index, 3) === '"""') {
                var start = index;
                var end = source.indexOf('"""', index + 3);
                var value;
                if (end === -1) {
                    value = source.substring(index + 3);
                    index = source.length;
                } else {
                    value = source.substring(index + 3, end);
                    index = end + 3;
                }
                tokens.push({ value: value, start: start, end: index });
            } else if (character === '"') {
                var start = index++;
                var value = '';
                while (index < source.length && source.charAt(index) !== '"') {
                    if (source.charAt(index) === '\\' && index + 1 < source.length) {
                        index++;
                    }
                    value += source.charAt(index++);
                }
                if (index < source.length) {
                    index++;
                }
                tokens.push({ value: value, start: start, end: index });
            } else if ('{}'.indexOf(character) !== -1) {
                tokens.push({ value: character, start: index, end: index + 1 });
                index++;
            } else {
                var start = index;
                while (index < source.length && !/\s/.test(source.charAt(index)) && '{}"'.indexOf(source.charAt(index)) === -1) {
                    index++;
                }
                tokens.push({ value: source.substring(start, index), start: start, end: index });
            }
        }

        return tokens;
    }

    function getMatchingBraces(tokens) {
        var starts = [];
        var braces = {};
        tokens.forEach(function(token, index) {
            if (token.value === '{') {
                starts.push(index);
            } else if (token.value === '}' && starts.length > 0) {
                braces[starts.pop()] = index;
            }
        });
        return braces;
    }

    function isInside(element, position) {
        return element.braceStart !== undefined && element.braceStart < position && position < element.braceEnd;
    }

    function parseModelElements() {
        var source = editor.getValue();
        var tokens = tokenizeDsl(source);
        var braces = getMatchingBraces(tokens);
        var modelStart;
        var modelEnd;

        tokens.forEach(function(token, index) {
            if (token.value === 'model' && braces[index + 1] !== undefined) {
                modelStart = tokens[index + 1].start;
                modelEnd = tokens[braces[index + 1]].start;
            }
        });

        var elements = [];
        var types = ['softwareSystem', 'container', 'component'];
        tokens.forEach(function(token, index) {
            if (types.indexOf(token.value) === -1 || modelStart === undefined || token.start < modelStart || token.start > modelEnd) {
                return;
            }

            var parent;
            elements.forEach(function(candidate) {
                if (isInside(candidate, token.start) && (parent === undefined || candidate.braceStart > parent.braceStart)) {
                    parent = candidate;
                }
            });

            if ((token.value === 'container' && (!parent || parent.type !== 'softwareSystem')) ||
                (token.value === 'component' && (!parent || parent.type !== 'container'))) {
                return;
            }

            var next = index + 1;
            var values = [];
            while (next < tokens.length && tokens[next].value !== '{' && tokens[next].value !== '}') {
                if (source.substring(tokens[next - 1].end, tokens[next].start).indexOf('\n') !== -1) {
                    break;
                }
                values.push(tokens[next]);
                next++;
            }
            var braceIndex = tokens[next] && tokens[next].value === '{' ? next : undefined;
            var headerEnd = braceIndex === undefined ? (values.length > 0 ? values[values.length - 1].end : token.end) : tokens[braceIndex].start;
            var element = {
                type: token.value,
                name: values[0] ? values[0].value : '',
                description: values[1] ? values[1].value : '',
                technology: token.value === 'softwareSystem' ? '' : (values[2] ? values[2].value : ''),
                tags: token.value === 'softwareSystem' ? (values[2] ? values[2].value : '') : (values[3] ? values[3].value : ''),
                start: token.start,
                headerEnd: headerEnd,
                braceStart: braceIndex === undefined ? undefined : tokens[braceIndex].start,
                braceEnd: braceIndex === undefined || braces[braceIndex] === undefined ? undefined : tokens[braces[braceIndex]].start,
                parent: parent
            };
            elements.push(element);
        });

        elements.forEach(function(element) {
            var siblings = elements.filter(function(candidate) { return candidate.parent === element.parent && candidate.type === element.type; });
            element.path = (element.parent ? element.parent.path + '/' : '') + element.type + ':' + siblings.indexOf(element);
            element.interfaces = [];
        });

        tokens.forEach(function(token, index) {
            if (token.value !== 'url' && token.value !== 'provides' && token.value !== 'consumes') {
                return;
            }

            var owner;
            elements.forEach(function(element) {
                if (isInside(element, token.start) && (owner === undefined || element.braceStart > owner.braceStart)) {
                    owner = element;
                }
            });
            if (!owner) {
                return;
            }

            var next = index + 1;
            var values = [];
            while (next < tokens.length && tokens[next].value !== '{' && tokens[next].value !== '}') {
                if (source.substring(tokens[next - 1].end, tokens[next].start).indexOf('\n') !== -1) {
                    break;
                }
                values.push(tokens[next]);
                next++;
            }
            var lineEnd = source.indexOf('\n', token.end);
            lineEnd = lineEnd === -1 ? source.length : lineEnd + 1;
            if (token.value === 'url') {
                owner.url = values[0] ? values[0].value : '';
                owner.urlStart = source.lastIndexOf('\n', token.start) + 1;
                owner.urlEnd = lineEnd;
            } else {
                var braceIndex = tokens[next] && tokens[next].value === '{' ? next : undefined;
                owner.interfaces.push({
                    type: token.value,
                    key: values[0] ? values[0].value : '',
                    action: token.value === 'provides' && values[1] ? values[1].value : '',
                    technology: values[token.value === 'provides' ? 2 : 1] ? values[token.value === 'provides' ? 2 : 1].value : '',
                    description: values[token.value === 'provides' ? 3 : 2] ? values[token.value === 'provides' ? 3 : 2].value : '',
                    tags: values[token.value === 'provides' ? 4 : 3] ? values[token.value === 'provides' ? 4 : 3].value : '',
                    start: token.start,
                    end: braceIndex === undefined ? (values.length > 0 ? values[values.length - 1].end : token.end) : tokens[braceIndex].start,
                    braceStart: braceIndex === undefined ? undefined : tokens[braceIndex].start,
                    braceEnd: braceIndex === undefined || braces[braceIndex] === undefined ? undefined : tokens[braces[braceIndex]].start
                });
            }
        });

        tokens.forEach(function(token, index) {
            if (['description', 'technology', 'tags', 'action'].indexOf(token.value) === -1) {
                return;
            }
            var next = index + 1;
            var values = [];
            while (next < tokens.length && tokens[next].value !== '{' && tokens[next].value !== '}') {
                if (source.substring(tokens[next - 1].end, tokens[next].start).indexOf('\n') !== -1) {
                    break;
                }
                values.push(tokens[next]);
                next++;
            }
            if (values.length === 0) {
                return;
            }
            var value = token.value === 'tags' ? values.map(function(item) { return item.value; }).join(',') : values[0].value;
            var interfaceOwner;
            elements.forEach(function(element) {
                element.interfaces.forEach(function(item) {
                    if (isInside(item, token.start) && (interfaceOwner === undefined || item.braceStart > interfaceOwner.braceStart)) {
                        interfaceOwner = item;
                    }
                });
            });
            if (interfaceOwner) {
                if (token.value !== 'action' || interfaceOwner.type === 'provides') {
                    interfaceOwner[token.value] = value;
                    interfaceOwner[token.value + 'Start'] = token.start;
                    interfaceOwner[token.value + 'End'] = values[values.length - 1].end;
                }
                return;
            }
            if (token.value === 'action') {
                return;
            }
            var owner;
            elements.forEach(function(element) {
                if (isInside(element, token.start) && (owner === undefined || element.braceStart > owner.braceStart)) {
                    owner = element;
                }
            });
            if (owner && (token.value !== 'technology' || owner.type !== 'softwareSystem')) {
                owner[token.value] = value;
                owner[token.value + 'Start'] = token.start;
                owner[token.value + 'End'] = values[values.length - 1].end;
            }
        });

        return { source: source, modelStart: modelStart, modelEnd: modelEnd, elements: elements };
    }

    function quoteDsl(value) {
        return '"' + (value || '').replace(/\\/g, '\\\\').replace(/"/g, '\\"').replace(/\r?\n/g, '\\n') + '"';
    }

    function dslValues(values) {
        var last = values.length - 1;
        while (last >= 0 && !values[last]) {
            last--;
        }
        return values.slice(0, last + 1).map(quoteDsl).join(' ');
    }

    function setDslSource(source) {
        editor.setValue(source, -1);
        hasUnparsedDSL = true;
        renderModelFormEditor();
    }

    function getSelectedModelElement() {
        var model = parseModelElements();
        return { model: model, element: model.elements.find(function(element) { return element.path === selectedModelElementPath; }) };
    }

    function getIndent(source, position) {
        var lineStart = source.lastIndexOf('\n', position) + 1;
        return source.substring(lineStart, position).match(/^\s*/)[0];
    }

    function elementHeader(element) {
        var values = [element.name, element.description];
        if (element.type === 'softwareSystem') {
            values.push(element.tags);
        } else {
            values.push(element.technology, element.tags);
        }
        return element.type + ' ' + dslValues(values);
    }

    function updateSelectedElement(field, value) {
        var selected = getSelectedModelElement();
        if (!selected.element) {
            return;
        }
        var element = selected.element;
        element[field] = value;
        var source = selected.model.source;
        if (field === 'url') {
            if (element.urlStart !== undefined) {
                source = source.substring(0, element.urlStart) + (value ? getIndent(source, element.urlStart) + 'url ' + quoteDsl(value) + '\n' : '') + source.substring(element.urlEnd);
            } else if (value && element.braceEnd !== undefined) {
                var lineStart = source.lastIndexOf('\n', element.braceEnd) + 1;
                source = source.substring(0, lineStart) + getIndent(source, element.braceEnd) + '    url ' + quoteDsl(value) + '\n' + source.substring(lineStart);
            } else if (value) {
                source = source.substring(0, element.headerEnd) + ' {\n' + getIndent(source, element.start) + '}' + source.substring(element.headerEnd);
                setDslSource(source);
                updateSelectedElement(field, value);
                return;
            }
        } else if (element[field + 'Start'] !== undefined) {
            source = source.substring(0, element[field + 'Start']) + field + ' ' + quoteDsl(value) + source.substring(element[field + 'End']);
        } else {
            source = source.substring(0, element.start) + elementHeader(element) + source.substring(element.headerEnd);
        }
        setDslSource(source);
    }

    function updateSelectedInterface(index, field, value) {
        var selected = getSelectedModelElement();
        if (!selected.element || !selected.element.interfaces[index]) {
            return;
        }
        var item = selected.element.interfaces[index];
        item[field] = value;
        var source;
        if (item[field + 'Start'] !== undefined) {
            source = selected.model.source.substring(0, item[field + 'Start']) + field + ' ' + quoteDsl(value) + selected.model.source.substring(item[field + 'End']);
        } else {
            var values = item.type === 'provides' ? [item.key, item.action, item.technology, item.description, item.tags] : [item.key, item.technology, item.description, item.tags];
            source = selected.model.source.substring(0, item.start) + item.type + ' ' + dslValues(values) + selected.model.source.substring(item.end);
        }
        setDslSource(source);
    }

    function addModelElement(parentPath) {
        var model = parseModelElements();
        var parent = parentPath ? model.elements.find(function(element) { return element.path === parentPath; }) : undefined;
        var type = parent ? (parent.type === 'softwareSystem' ? 'container' : 'component') : 'softwareSystem';
        if (parent && parent.type === 'component') {
            return;
        }
        if (!parent && model.modelEnd === undefined) {
            showError('A model block is required before elements can be added.');
            return;
        }
        var source = model.source;
        var name = type === 'softwareSystem' ? 'Software System' : (type === 'container' ? 'Container' : 'Component');
        var insertAt = parent ? parent.braceEnd : model.modelEnd;
        if (insertAt === undefined) {
            source = source.substring(0, parent.headerEnd) + ' {\n' + getIndent(source, parent.start) + '}' + source.substring(parent.headerEnd);
            setDslSource(source);
            addModelElement(parentPath);
            return;
        }
        var lineStart = source.lastIndexOf('\n', insertAt) + 1;
        var indent = parent ? getIndent(source, parent.braceEnd) + '    ' : getIndent(source, model.modelEnd) + '    ';
        var statement = type + ' ' + quoteDsl(name) + (type === 'softwareSystem' ? '' : ' "" ""') + ' {\n' + indent + '}';
        source = source.substring(0, lineStart) + indent + statement + '\n' + source.substring(lineStart);
        setDslSource(source);
        var updated = parseModelElements();
        var siblings = updated.elements.filter(function(element) { return element.parent && parentPath ? element.parent.path === parentPath && element.type === type : !element.parent && element.type === type; });
        selectedModelElementPath = siblings.length > 0 ? siblings[siblings.length - 1].path : selectedModelElementPath;
        renderModelFormEditor();
    }

    function addInterface(type) {
        var selected = getSelectedModelElement();
        if (!selected.element || selected.element.braceEnd === undefined) {
            return;
        }
        var element = selected.element;
        var lineStart = selected.model.source.lastIndexOf('\n', element.braceEnd) + 1;
        var indent = getIndent(selected.model.source, element.braceEnd) + '    ';
        var source = selected.model.source.substring(0, lineStart) + indent + type + ' "Key"\n' + selected.model.source.substring(lineStart);
        setDslSource(source);
    }

    function modelFormInput(label, field, value, multiline) {
        var control = multiline ? '<textarea class="form-control" rows="3" data-model-field="' + field + '">' : '<input class="form-control" type="text" data-model-field="' + field + '" value="';
        control += structurizr.util.escapeHtml(value || '');
        control += multiline ? '</textarea>' : '">';
        return '<div class="model-form-field"><label class="form-label">' + label + '</label>' + control + '</div>';
    }

    function modelFormInterfaceInput(label, index, field, value) {
        return '<div class="col-md-6 model-form-field"><label class="form-label small">' + label + '</label><input class="form-control form-control-sm" type="text" data-interface-index="' + index + '" data-interface-field="' + field + '" value="' + structurizr.util.escapeHtml(value || '') + '"></div>';
    }

    function renderModelFormTree(elements) {
        var html = '<div class="model-form-tree-item' + (selectedModelElementPath === undefined ? ' selected' : '') + '" data-model-select=""><button class="model-form-tree-toggle" type="button"></button><span>Workspace</span><button class="model-form-tree-add" type="button" title="Add a software system" data-model-add=""><img src="/static/bootstrap-icons/plus-lg.svg" alt="Add"></button></div>';

        function renderChildren(parent, depth) {
            elements.filter(function(element) { return element.parent === parent; }).forEach(function(element) {
                var children = elements.filter(function(candidate) { return candidate.parent === element; });
                var collapsed = collapsedModelElementPaths[element.path] === true;
                var addTitle = element.type === 'softwareSystem' ? 'Add a container' : (element.type === 'container' ? 'Add a component' : '');
                html += '<div class="model-form-tree-item' + (selectedModelElementPath === element.path ? ' selected' : '') + '" style="margin-left: ' + ((depth + 1) * 18) + 'px" data-model-select="' + element.path + '">';
                if (children.length > 0) {
                    html += '<button class="model-form-tree-toggle" type="button" data-model-toggle="' + element.path + '" title="' + (collapsed ? 'Expand' : 'Collapse') + '"><img src="/static/bootstrap-icons/caret-' + (collapsed ? 'right' : 'down') + '.svg" alt=""></button>';
                } else {
                    html += '<button class="model-form-tree-toggle" type="button"></button>';
                }
                html += '<span>' + structurizr.util.escapeHtml(element.name || ('Unnamed ' + element.type)) + '</span>';
                if (addTitle) {
                    html += '<button class="model-form-tree-add" type="button" title="' + addTitle + '" data-model-add="' + element.path + '"><img src="/static/bootstrap-icons/plus-lg.svg" alt="Add"></button>';
                }
                html += '</div>';
                if (!collapsed) {
                    renderChildren(element, depth + 1);
                }
            });
        }

        renderChildren(undefined, 0);
        $('#modelFormTree').html(html);
    }

    function renderModelFormDetails(elements) {
        var element = elements.find(function(candidate) { return candidate.path === selectedModelElementPath; });
        if (!element) {
            if (elements.length > 0) {
                selectedModelElementPath = elements[0].path;
                renderModelFormTree(elements);
                renderModelFormDetails(elements);
                return;
            }
            $('#modelFormDetails').html('<p class="text-muted">Add a software system to begin modelling your architecture.</p>');
            return;
        }

        var title = element.type === 'softwareSystem' ? 'Software system' : (element.type === 'container' ? 'Container' : 'Component');
        var html = '<h5>' + title + '</h5>';
        html += modelFormInput('Name', 'name', element.name);
        html += modelFormInput('Description', 'description', element.description, true);
        if (element.type !== 'softwareSystem') {
            html += modelFormInput('Technology', 'technology', element.technology);
        }
        html += modelFormInput('Tags', 'tags', element.tags);
        html += modelFormInput('URL', 'url', element.url);
        html += '<div class="model-form-section-title">Interfaces <button class="btn btn-sm btn-outline-secondary" type="button" data-add-interface="provides"><img src="/static/bootstrap-icons/plus-lg.svg" class="icon-xs" alt=""> Provides</button><button class="btn btn-sm btn-outline-secondary ms-2" type="button" data-add-interface="consumes"><img src="/static/bootstrap-icons/plus-lg.svg" class="icon-xs" alt=""> Consumes</button></div>';

        if (element.interfaces.length === 0) {
            html += '<p class="text-muted small">No interfaces have been defined.</p>';
        }
        element.interfaces.forEach(function(item, index) {
            html += '<div class="model-form-interface"><strong>' + (item.type === 'provides' ? 'Provides' : 'Consumes') + '</strong><div class="row mt-2">';
            html += modelFormInterfaceInput('Key', index, 'key', item.key);
            if (item.type === 'provides') {
                html += modelFormInterfaceInput('Action', index, 'action', item.action);
            }
            html += modelFormInterfaceInput('Technology', index, 'technology', item.technology);
            html += modelFormInterfaceInput('Description', index, 'description', item.description);
            html += modelFormInterfaceInput('Tags', index, 'tags', item.tags);
            html += '</div></div>';
        });
        $('#modelFormDetails').html(html);
    }

    function bindModelFormEvents() {
        $('#modelFormTree [data-model-select]').click(function(event) {
            if ($(event.target).closest('button').length > 0) {
                return;
            }
            selectedModelElementPath = $(this).attr('data-model-select') || undefined;
            renderModelFormEditor();
        });
        $('#modelFormTree [data-model-toggle]').click(function() {
            var path = $(this).attr('data-model-toggle');
            collapsedModelElementPaths[path] = !collapsedModelElementPaths[path];
            renderModelFormEditor();
        });
        $('#modelFormTree [data-model-add]').click(function() {
            addModelElement($(this).attr('data-model-add') || undefined);
        });
        $('#modelFormDetails [data-model-field]').change(function() {
            updateSelectedElement($(this).attr('data-model-field'), $(this).val());
        });
        $('#modelFormDetails [data-interface-field]').change(function() {
            updateSelectedInterface(parseInt($(this).attr('data-interface-index')), $(this).attr('data-interface-field'), $(this).val());
        });
        $('#modelFormDetails [data-add-interface]').click(function() {
            addInterface($(this).attr('data-add-interface'));
        });
    }

    function renderModelFormEditor() {
        if (!editorRendered) {
            return;
        }
        var model = parseModelElements();
        renderModelFormTree(model.elements);
        renderModelFormDetails(model.elements);
        bindModelFormEvents();
    }

