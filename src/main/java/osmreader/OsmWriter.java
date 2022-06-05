package osmreader;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.pbf.seq.PbfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OsmWriter {
    private final OutputStream outputStream;
    private final OsmOutputStream osmStream;
    public OsmWriter(String outputFileName) throws FileNotFoundException {
        outputStream = new FileOutputStream(outputFileName);
        osmStream = new PbfWriter(outputStream, false);
    }

    public void writeNode(OsmNode node) throws IOException {
        osmStream.write(node);
    }

    public void close() {
        try {
            osmStream.complete();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
