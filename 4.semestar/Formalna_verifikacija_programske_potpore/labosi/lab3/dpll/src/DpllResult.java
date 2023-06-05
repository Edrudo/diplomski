import java.util.HashMap;
import java.util.Iterator;

public class DpllResult {

  private HashMap<String, Boolean> map;
  private long timeTaken;

  public DpllResult(HashMap<String, Boolean> map) {
    this.map = map;
  }

  public void setTimeTaken(long time) {
    this.timeTaken = time;
  }

  public long getTimeTaken() {
    return timeTaken;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator iterator = map.keySet().iterator();
    while(iterator.hasNext()) {
      String literal = (String) iterator.next();
      boolean value = map.get(literal);
      sb.append(literal + " -> ");
      sb.append(value);
      if(iterator.hasNext()) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  public HashMap<String, Boolean> getMap() {
    return map;
  }

}
