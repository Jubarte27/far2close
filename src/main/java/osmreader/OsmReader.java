package osmreader;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.pbf.seq.PbfIterator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class OsmReader {
    private final InputStream stream;

    private OsmIterator iterator() {
        return new PbfIterator(stream, false);
    }

    public OsmReader(String resourcePath) {
        stream = getClass().getResourceAsStream(resourcePath);
    }

    public ArrayList<OsmNode> filteredNodes(Map<String, String> tags, int max) {
        return filterAndConvert(tags, n -> n, max);
    }

    public <T> ArrayList<T> filterAndConvert(Map<String, String> tags, Function<OsmNode, T> converter, int max) {
        ArrayList<T> filtered = new ArrayList<>(max);
        for (EntityContainer container : iterator()) {
            if (container.getType() == EntityType.Node) {
                OsmNode node = (OsmNode) container.getEntity();
                Map<String, String> nodeTags = OsmModelUtil.getTagsAsMap(node);

                boolean shouldAdd = tags.entrySet().stream().allMatch(desiredTag -> Objects.equals(nodeTags.get(desiredTag.getKey()), desiredTag.getValue()));
                if (shouldAdd) {
                    filtered.add(converter.apply(node));
                }
                if (filtered.size() >= max) {
                    break;
                }
            }
        }
        filtered.trimToSize();
        return filtered;
    }

    public ArrayList<OsmNode> all() {
        ArrayList<OsmNode> allNodes = new ArrayList<>();
        for (EntityContainer container : iterator()) {
            if (container.getType() == EntityType.Node) {
                OsmNode node = (OsmNode) container.getEntity();
                allNodes.add(node);
            }
        }
        return allNodes;
    }

    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
