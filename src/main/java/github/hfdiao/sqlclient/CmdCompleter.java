package github.hfdiao.sqlclient;

import java.util.LinkedList;
import java.util.Stack;

import org.gnu.readline.ReadlineCompleter;

/**
 * @author dhf
 */
public class CmdCompleter implements ReadlineCompleter {
    public CmdCompleter() {}

    public CmdCompleter(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    private int maxHistory = 1000;

    private LinkedList<String> history = new LinkedList<String>();

    private Stack<String> possibleValues;

    public void addHistory(String h) {
        if (history.size() >= maxHistory) {
            history.removeFirst();
        }
        history.add(h);
    }

    @Override
    public String completer(String text, int state) {
        if (0 == state) {
            possibleValues = new Stack<String>();
            for (String h: history) {
                if (h.indexOf(text) != -1) {
                    possibleValues.push(h);
                }
            }
        }
        if (null != possibleValues && !possibleValues.isEmpty()) {
            return possibleValues.pop();
        }
        return null;
    }

}
