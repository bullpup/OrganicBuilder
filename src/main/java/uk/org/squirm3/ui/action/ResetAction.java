package uk.org.squirm3.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.ui.Messages;

public class ResetAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;

    public ResetAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon resetIcon) {
        this.applicationEngine = applicationEngine;
        putValue(Action.SHORT_DESCRIPTION,
                Messages.localize("simulation.reset", messageSource));
        putValue(Action.SMALL_ICON, resetIcon);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        applicationEngine.restartLevel();
    }
}
