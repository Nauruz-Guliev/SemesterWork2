package Protocol.Message.models;

import java.io.Serializable;

public class Way implements Serializable {

    private final City start;
    private final City end;

    private final double length;

    public Way(City start, City end) {
        this.start = start;
        this.end = end;
        this.length = Math.sqrt(  Math.pow((start.x() - end.x()), 2) + Math.pow((start.y() - end.y()), 2)  );
    }

    public City getStart() {
        return start;
    }

    public City getEnd() {
        return end;
    }

    public double getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Way{" +
                "start=" + start +
                ", end=" + end +
                ", length=" + length +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Way way = (Way) o;

        return start.equals(way.start) && end.equals(way.end) ||
                start.equals(way.end) && end.equals(way.start);

    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result += end != null ? end.hashCode() : 0;
        return result;
    }
}
