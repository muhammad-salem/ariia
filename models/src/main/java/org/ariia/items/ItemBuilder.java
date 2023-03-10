package org.ariia.items;

import org.ariia.args.Argument;
import org.ariia.config.Properties;
import org.ariia.util.Utils;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * build only items from arguments
 *
 * @author salem
 */

public class ItemBuilder {

    private Argument arguments;
    private Properties properties;
    private List<Item> items;

    public ItemBuilder(Argument arguments, Properties properties) {
        this.properties = properties;
        setArguments(arguments);
    }

    public List<Item> getItems() {
        return items;
    }

    public void clear() {
        this.items.clear();
        this.items = null;
    }

    public void setArguments(Argument arguments) {
        this.arguments = arguments;
        initItems();
    }

    private void initItems() {
        this.items = new LinkedList<>();
        if (arguments.isUrl()) {
            downloadUrl();
        } else if (arguments.isInputFile()) {
            downloadInputFile();
        } else if (arguments.isMetaLink()) {
            downloadMetalink();
        } else if (arguments.isStream()) {
            streamUrl(arguments);
        }
    }

    private void addItem(String url, Map<String, List<String>> headers) {
        var item = buildItemOf(url, headers);
        items.add(item);
    }

    public Item buildItemOf(String url) {
        return buildItemOf(url, Collections.emptyMap());
    }

    public Item buildItemOf(String url, Map<String, List<String>> headers) {
        var item = new Item();
        item.setUrl(url);
        item.setHeaders(headers);
        buildItemCommon(item);
        return item;
    }

    public Item buildItemCommon(Item item) {
        if (arguments.isCookieFile()) {
            var cookies = arguments.getCookies();
            var values = new ArrayList<String>(cookies.size());
            // String cookie = "";
            for (var entry : cookies.entrySet()) {
                values.add(entry.getKey() + '=' + entry.getValue());
                // cookie = entry.getKey() + '=' + entry.getValue() + "; " + cookie;
            }
            item.addHeader("Cookie", values);
        }
        if (arguments.isSavePath())
            item.setSaveDirectory(arguments.getSavePath());
        else {
            item.setSaveDirectory(properties.getDefaultSaveDirectory());
        }
        if (arguments.isFileName()) {
            item.setFilename(arguments.getFileName());
        } else {
            var file = new File(item.getUrl());
            var fileName = file.getName().split("\\?")[0];

            if ("".equals(fileName)) {
                String[] fileParts = item.getUrl().split("/");
                fileName = fileParts[fileParts.length - 2].split("\\?")[0];
            }
            item.setFilename(fileName);
        }
        return item;
    }

    public void downloadUrl() {
        for (var url : arguments.getUrls()) {
            addItem(url, arguments.getHeaders());
        }
    }

    private void streamUrl(Argument arguments) {
        addItem(arguments.getStream(), arguments.getHeaders());
    }

    private void downloadInputFile() {
        var lines = Utils.readLines(arguments.getInputFile());
        var iterator = lines.iterator();
        Map<String, List<String>> headers = null;
        while (iterator.hasNext()) {
            var line = iterator.next();
            if (line.startsWith("#")) {
                continue;
            } else if (line.startsWith("http")) {
                headers = new LinkedHashMap<>();
                addItem(line.trim(), headers);
            } else if (line.equals("\t")) {
                continue;
            } else if (line.startsWith("\\t") || line.startsWith(" ") || line.startsWith("	")) {
                var header = line.trim().split(": ");
                if (header.length == 1) {
                    continue;
                }
                var value = headers.getOrDefault(header[0], new ArrayList<>(1));
                value.add(header[1]);
                headers.put(header[0], value);
            }
        }
    }

    private void downloadMetalink() {
        MetaLinkItem item = null;
        var metaLinkFile = arguments.getMetaLinkFile();
        if (metaLinkFile.contains(".metalink")) {
            item = readMetaLink(metaLinkFile);
        } else if (metaLinkFile.contains(".xml")) {
            item = readMetaLinkXML(metaLinkFile);
        } else {
            item = readMetaLinkText(metaLinkFile);
        }
        if (Objects.nonNull(item)) {
            buildItemCommon(item);
            items.add(item);
        }
    }

    private MetaLinkItem readMetaLink(String metaLinkFile) {

        var factory = DocumentBuilderFactory.newInstance();
        try {
            var builder = factory.newDocumentBuilder();
            var document = builder.parse(new File(metaLinkFile));
            var mirrors = document.getElementsByTagName("url");
            var urls = new ArrayList<String>();

            for (int i = 0; i < mirrors.getLength(); i++) {
                var node = mirrors.item(i);
                if (node.hasAttributes() && node.getAttributes().getNamedItem("type").getNodeValue().equals("http")){
                    urls.add(node.getTextContent());
                }
            }
            var iterator = urls.iterator();
            return readMetaLinkText(iterator);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MetaLinkItem readMetaLinkText(String metaLinkFile) {
        var urls = Utils.readLines(metaLinkFile);
        var iterator = urls.iterator();
        return readMetaLinkText(iterator);
    }

    private MetaLinkItem readMetaLinkText(Iterator<String> iterator) {
        var builder = new MetaLinkItem();
        Map<String, List<String>> headers = new LinkedHashMap<>();

        while (iterator.hasNext()) {
            var string = iterator.next();
            if (string.startsWith("#")) {
                iterator.remove();
            } else if (string.startsWith("http")) {
                builder.addMirror(string);
            } else if (string.startsWith("\t")) {
                var index = string.indexOf(": ");
                var headerName = string.substring(1, index);
                var headerValue = string.substring(index + 2);
                var value = headers.getOrDefault(headerName, new ArrayList<>());
                value.add(headerValue);
                headers.put(headerName, value);
            }
        }
        builder.setHeaders(headers);
        return builder;
    }

    public MetaLinkItem readMetaLinkXML(String metaLinkFile) {

        var factory = DocumentBuilderFactory.newInstance();
        try {
            var builder = factory.newDocumentBuilder();
            var document = builder.parse(new File(metaLinkFile));
            var mirrors = document.getElementsByTagName("mirror");
            var urls = new ArrayList<String>();

            for (int i = 0; i < mirrors.getLength(); i++) {
                Node node = mirrors.item(i);
                urls.add(node.getAttributes().getNamedItem("url").getNodeValue());
            }
            var iterator = urls.iterator();
            return readMetaLinkText(iterator);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
