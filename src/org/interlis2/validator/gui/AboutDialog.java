package org.interlis2.validator.gui;

import org.interlis2.validator.Main;

import javax.swing.*;
import java.awt.*;

/**
 * @author philippluca
 */
public class AboutDialog extends JDialog {

    private javax.swing.JPanel jContentPane = null;
    private javax.swing.JPanel infoPanel = null;
    private javax.swing.JPanel buttonPanel = null;
    private javax.swing.JButton okButton = null;
    private javax.swing.JLabel programVersionLabel = null;
    private javax.swing.JLabel programVersion = null;
    private javax.swing.JLabel iliCompilerVersionLabel = null;
    private javax.swing.JLabel iliCompilerVersion = null;
    private javax.swing.JLabel ioxIliVersionLabel = null;
    private javax.swing.JLabel ioxIliVersion = null;
    private javax.swing.JLabel javaVersionLabel = null;
    private javax.swing.JLabel javaVmVersionLabel = null;
    private javax.swing.JLabel javaVersion = null;
    private javax.swing.JLabel javaVmVersion = null;


    public AboutDialog(java.awt.Frame owner) {
        super(owner, true);
        initialize();
        getProgramVersion().setText(Main.getVersion());
        getIliCompilerVersion().setText(ch.interlis.ili2c.metamodel.TransferDescription.getVersion());
        getIoxIliVersion().setText(ch.interlis.iox_j.utility.IoxUtility.getVersion());
        getJavaVersion().setText(System.getProperty("java.version"));
        getJavaVmVersion().setText(System.getProperty("java.vm.version"));
        this.pack();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setTitle("About " + Main.APP_NAME);
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getInfoPanel(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
            jContentPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        }
        return jContentPane;
    }

    /**
     * This method initializes infoPanel with all children
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getInfoPanel() {
        if (infoPanel == null) {
            infoPanel = new javax.swing.JPanel();
            java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints5 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints6 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints7 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints8 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints9 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints10 = new java.awt.GridBagConstraints();

            consGridBagConstraints1.gridx = 0;
            consGridBagConstraints1.gridy = 0;
            consGridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints1.fill = GridBagConstraints.BOTH;
            consGridBagConstraints1.insets = new java.awt.Insets(0, 0, 5, 12);
            consGridBagConstraints2.gridx = 1;
            consGridBagConstraints2.gridy = 0;
            consGridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints2.fill = GridBagConstraints.BOTH;

            consGridBagConstraints3.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints3.gridx = 0;
            consGridBagConstraints3.gridy = 1;
            consGridBagConstraints3.insets = new java.awt.Insets(0, 0, 5, 12);
            consGridBagConstraints4.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints4.gridx = 1;
            consGridBagConstraints4.gridy = 1;

            consGridBagConstraints5.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints5.gridx = 0;
            consGridBagConstraints5.gridy = 2;
            consGridBagConstraints5.insets = new java.awt.Insets(0, 0, 5, 12);
            consGridBagConstraints6.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints6.gridx = 1;
            consGridBagConstraints6.gridy = 2;

            consGridBagConstraints7.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints7.gridx = 0;
            consGridBagConstraints7.gridy = 3;
            consGridBagConstraints7.insets = new java.awt.Insets(0, 0, 5, 12);
            consGridBagConstraints8.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints8.gridx = 1;
            consGridBagConstraints8.gridy = 3;
            consGridBagConstraints8.insets = new java.awt.Insets(0, 0, 5, 12);

            consGridBagConstraints9.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints9.gridx = 0;
            consGridBagConstraints9.gridy = 4;
            consGridBagConstraints10.anchor = java.awt.GridBagConstraints.NORTHWEST;
            consGridBagConstraints10.gridx = 1;
            consGridBagConstraints10.gridy = 4;

            infoPanel.setLayout(new java.awt.GridBagLayout());
            infoPanel.add(getProgramVersionLabel(), consGridBagConstraints1);
            infoPanel.add(getProgramVersion(), consGridBagConstraints2);
            infoPanel.add(getIliCompilerVersionLabel(), consGridBagConstraints3);
            infoPanel.add(getIliCompilerVersion(), consGridBagConstraints4);
            infoPanel.add(getIoxIliVersionLabel(), consGridBagConstraints5);
            infoPanel.add(getIoxIliVersion(), consGridBagConstraints6);
            infoPanel.add(getJavaVersionLabel(), consGridBagConstraints7);
            infoPanel.add(getJavaVersion(), consGridBagConstraints8);
            infoPanel.add(getJavaVmVersionLabel(), consGridBagConstraints9);
            infoPanel.add(getJavaVmVersion(), consGridBagConstraints10);
        }
        return infoPanel;
    }

    private javax.swing.JLabel getProgramVersionLabel() {
        if (programVersionLabel == null) {
            programVersionLabel = new javax.swing.JLabel();
            programVersionLabel.setText("Program version");
        }
        return programVersionLabel;
    }

    private javax.swing.JLabel getProgramVersion() {
        if (programVersion == null) {
            programVersion = new javax.swing.JLabel();
        }
        return programVersion;
    }

    private javax.swing.JLabel getIliCompilerVersionLabel() {
        if (iliCompilerVersionLabel == null) {
            iliCompilerVersionLabel = new javax.swing.JLabel();
            iliCompilerVersionLabel.setText("ili2c version");
        }
        return iliCompilerVersionLabel;
    }

    private javax.swing.JLabel getIliCompilerVersion() {
        if (iliCompilerVersion == null) {
            iliCompilerVersion = new javax.swing.JLabel();
        }
        return iliCompilerVersion;
    }

    private javax.swing.JLabel getIoxIliVersionLabel() {
        if (ioxIliVersionLabel == null) {
            ioxIliVersionLabel = new javax.swing.JLabel();
            ioxIliVersionLabel.setText("iox-ili version");
        }
        return ioxIliVersionLabel;
    }

    private javax.swing.JLabel getIoxIliVersion() {
        if (ioxIliVersion == null) {
            ioxIliVersion = new javax.swing.JLabel();
        }
        return ioxIliVersion;
    }

    private javax.swing.JLabel getJavaVersionLabel() {
        if (javaVersionLabel == null) {
            javaVersionLabel = new javax.swing.JLabel();
            javaVersionLabel.setText("Java version");
        }
        return javaVersionLabel;
    }

    private javax.swing.JLabel getJavaVersion() {
        if (javaVersion == null) {
            javaVersion = new javax.swing.JLabel();
        }
        return javaVersion;
    }

    private javax.swing.JLabel getJavaVmVersionLabel() {
        if (javaVmVersionLabel == null) {
            javaVmVersionLabel = new javax.swing.JLabel();
            javaVmVersionLabel.setText("Java-VM version");
        }
        return javaVmVersionLabel;
    }

    private javax.swing.JLabel getJavaVmVersion() {
        if (javaVmVersion == null) {
            javaVmVersion = new javax.swing.JLabel();
        }
        return javaVmVersion;
    }

    private javax.swing.JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new javax.swing.JPanel();
            buttonPanel.add(getOkButton(), null);
        }
        return buttonPanel;
    }

    private javax.swing.JButton getOkButton() {
        if (okButton == null) {
            okButton = new javax.swing.JButton();
            okButton.setText("OK");
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return okButton;
    }

}
