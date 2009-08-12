package uk.org.squirm3.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.ILevelListener;

/**  
${my.copyright}
 */

public class LevelNavigatorView implements IView, ILevelListener {
    private ApplicationEngine applicationEngine;

    private final Action intro, previous, next, last;
    private final JComboBox levelComboBox;

    private boolean update;


    public LevelNavigatorView(ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
        intro = createIntroAction();
        previous = createPreviousAction();
        levelComboBox = createLevelComboBox();
        next = createNextAction();
        last = createLastAction();
        applicationEngine.getEngineDispatcher().addLevelListener(this);
        update = true;
        levelHasChanged();
    }

    public Action getIntroAction() {
        return intro;
    }

    public Action getPreviousAction() {
        return previous;
    }

    public Action getNextAction() {
        return next;
    }

    public Action getLastAction() {
        return last;
    }

    public JComboBox getLevelComboBox() {
        return levelComboBox;
    }

    private JComboBox createLevelComboBox() {
        List levelList = applicationEngine.getLevelManager().getLevels();
        String[] levelsLabels = new String[levelList.size()];
        Iterator it = levelList.iterator();
        int i = 0;
        while(it.hasNext()) {
            String number = String.valueOf(i)+"  ";
            if(i<10) number += "  ";
            levelsLabels[i] = number+((Level)it.next()).getTitle();
            i++;
        }
        final JComboBox cb = new JComboBox(levelsLabels);
        cb.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!update) applicationEngine.goToLevel(levelComboBox.getSelectedIndex(),null);
                        update = false;
                    }
                });
        cb.setToolTipText(Application.localize(new String[] {"interface","navigation","selected"}));
        return cb;
    }

    private Action createIntroAction() {
        final Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                applicationEngine.goToFirstLevel();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","navigation","first"}));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("first"));
        return 	action;
    }

    private Action createPreviousAction() {
        final Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                applicationEngine.goToPreviousLevel();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","navigation","previous"}));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("previous"));
        return 	action;
    }

    private Action createNextAction() {
        final Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                applicationEngine.goToNextLevel();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","navigation","next"}));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("next"));
        return 	action;
    }

    private Action createLastAction() {
        final Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                applicationEngine.goToLastLevel();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","navigation","last"}));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("last"));
        return 	action;
    }

    private void updateControls() {
        final List levelList = applicationEngine.getLevelManager().getLevels();
        final int levelNumber = levelList.indexOf(applicationEngine.getLevelManager().getCurrentLevel());

        final boolean firstLevel = levelNumber==0;
        intro.setEnabled(!firstLevel);
        previous.setEnabled(!firstLevel);

        final boolean lastLevel = levelNumber==(levelList.size()-1);
        last.setEnabled(!lastLevel);
        next.setEnabled(!lastLevel);

        // quick fix TODO chang
        update = true;
        levelComboBox.setSelectedIndex(levelNumber);
    }

    public void levelHasChanged() {
        updateControls();
    }

    public void configurationHasChanged() {}
}