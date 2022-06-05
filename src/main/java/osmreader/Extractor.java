package osmreader;

import bktree.point.Coordinate;
import de.topobyte.osm4j.core.model.iface.OsmNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Extractor {
    public static void main(String[] args) {
        OsmReader reader = new OsmReader("/south-america-latest.osm.pbf");
//        OsmReader reader = new OsmReader("/universities.osm.pbf");
//        OsmReader reader = new OsmReader("/schools.osm.pbf");
//        Map<String, String> filters = Map.of("amenity", "fast_food");
        Map<String, String> filters = Map.of();
        ArrayList<OsmNode> universities = reader.filteredNodes(filters, 1 << 30);

        System.out.println(universities.size());

//        try {
//            OsmWriter writer = new OsmWriter("fast_food.osm.pbf");
//            for (OsmNode university : universities) {
//                writer.writeNode(university);
//            }
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
