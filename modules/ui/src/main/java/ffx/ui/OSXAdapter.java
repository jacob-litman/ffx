/**
 * Title: Force Field X.
 *
 * Description: Force Field X - Software for Molecular Biophysics.
 *
 * Copyright: Copyright (c) Michael J. Schnieders 2001-2016.
 *
 * This file is part of Force Field X.
 *
 * Force Field X is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * Force Field X is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Force Field X; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package ffx.ui;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The OSXAdapter class was developed by following an example supplied on the OS
 * X site. It handles events generated by the following standard OS X toolbar
 * items: About, Preferences, Quit and File Associations
 *
 * @author Michael J. Schnieders
 *
 */
public class OSXAdapter implements AboutHandler, OpenFilesHandler,
        QuitHandler, PreferencesHandler {

    private final MainPanel mainPanel;
    private final Application application;
    private static final Logger logger = Logger.getLogger(OSXAdapter.class.getName());

    public OSXAdapter(MainPanel m) {
        mainPanel = m;
        application = Application.getApplication();
        application.setOpenFileHandler(this);
        application.setQuitHandler(this);

        application.setPreferencesHandler(this);
        application.setEnabledPreferencesMenu(true);

        application.setAboutHandler(this);
        application.setEnabledAboutMenu(true);

        ImageIcon icon = new ImageIcon(m.getClass().getClassLoader().getResource("ffx/ui/icons/icon64.png"));
        application.setDockIconImage(icon.getImage());
        application.setDockIconBadge("FFX");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append(application).toString();
    }

    @Override
    public void handleAbout(AppEvent.AboutEvent ae) {
        if (mainPanel != null) {
            mainPanel.about();
        }
    }

    @Override
    public void openFiles(AppEvent.OpenFilesEvent ofe) {
        List<File> files = ofe.getFiles();
        if (files == null) {
            return;
        }

        String filenames[] = new String[files.size()];
        int index = 0;
        for (File file : files) {
            filenames[index++] = file.getAbsolutePath();
        }

        if (mainPanel != null) {
            mainPanel.open(filenames);
        }
    }

    @Override
    public void handleQuitRequestWith(AppEvent.QuitEvent qe, QuitResponse qr) {
        if (mainPanel != null) {
            mainPanel.exit();
        } else {
            System.exit(-1);
        }
    }

    @Override
    public void handlePreferences(AppEvent.PreferencesEvent pe) {
        if (mainPanel != null) {
            mainPanel.getGraphics3D().preferences();
        }
    }

    /**
     * Set Mac OS X Systems Properties to promote native integration. How soon
     * do these need to be set to be recognized?
     */
    public static void setOSXProperties() {
        System.setProperty("apple.awt.brushMetalLook", "true");
        System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
        System.setProperty("apple.awt.showGrowBox", "true");
        System.setProperty("apple.awt.textantialiasing", "true");

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        System.setProperty("apple.macos.smallTabs", "true");

        System.setProperty("apple.mrj.application.apple.menu.about.name", "Force Field X");
        System.setProperty("apple.mrj.application.growbox.intrudes", "false");
        System.setProperty("apple.mrj.application.live-resize", "true");

        // -Xdock:name="Force Field X"
    }
}
