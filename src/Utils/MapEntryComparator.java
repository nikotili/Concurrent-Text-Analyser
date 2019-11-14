package Utils;

import java.util.Comparator;
import java.util.Map;

public class MapEntryComparator implements Comparator<Map.Entry<? extends Sequence, Long>> {


    @Override
    public int compare(Map.Entry<? extends Sequence, Long> e1, Map.Entry<? extends Sequence, Long> e2) {
        return e1.getValue().compareTo(e2.getValue());
    }
}
