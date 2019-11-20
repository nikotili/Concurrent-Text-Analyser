package al.unyt.edu.advjava.fall2019.assign01.Utils;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MapEntryComparator implements Comparator<Map.Entry<? extends Sequence, AtomicLong>> {


    @Override
    public int compare(Map.Entry<? extends Sequence, AtomicLong> e1, Map.Entry<? extends Sequence, AtomicLong> e2) {
        return (int) (e1.getValue().longValue() - e2.getValue().longValue());
    }
}
