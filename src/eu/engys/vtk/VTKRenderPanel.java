/*******************************************************************************
 *  |       o                                                                   |
 *  |    o     o       | HELYX-OS: The Open Source GUI for OpenFOAM             |
 *  |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 *  |    o     o       | http://www.engys.com                                   |
 *  |       o          |                                                        |
 *  |---------------------------------------------------------------------------|
 *  |   License                                                                 |
 *  |   This file is part of HELYX-OS.                                          |
 *  |                                                                           |
 *  |   HELYX-OS is free software; you can redistribute it and/or modify it     |
 *  |   under the terms of the GNU General Public License as published by the   |
 *  |   Free Software Foundation; either version 2 of the License, or (at your  |
 *  |   option) any later version.                                              |
 *  |                                                                           |
 *  |   HELYX-OS is distributed in the hope that it will be useful, but WITHOUT |
 *  |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 *  |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 *  |   for more details.                                                       |
 *  |                                                                           |
 *  |   You should have received a copy of the GNU General Public License       |
 *  |   along with HELYX-OS; if not, write to the Free Software Foundation,     |
 *  |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
 *******************************************************************************/
package eu.engys.vtk;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.CameraManager;
import eu.engys.gui.view3D.CameraManager.Position;
import eu.engys.gui.view3D.Interactor;
import eu.engys.gui.view3D.RenderPanel;
import eu.engys.gui.view3D.Representation;
import eu.engys.util.PrefUtil;
import eu.engys.util.plaf.ILookAndFeel;
import eu.engys.util.ui.ExecUtil;
import vtk.vtkActorCollection;
import vtk.vtkAssembly;
import vtk.vtkImageData;
import vtk.vtkLight;
import vtk.vtkObject;
import vtk.vtkPanel;
import vtk.vtkRenderer;
import vtk.vtkWindowToImageFilter;

public class VTKRenderPanel extends vtkPanel implements RenderPanel {

    private enum LowRendering {
        ON, OFF
    }

    private static final Logger logger = LoggerFactory.getLogger(VTKRenderPanel.class);

    public class DelayTimer extends Timer implements ActionListener {
        public DelayTimer() {
            super(PrefUtil.getInt(PrefUtil._3D_LOCK_INTRACTIVE_TIME, 2000), null);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent evt) {
            InteractiveOff();
        }
    }

    public class CountdownTimer extends Timer implements ActionListener {

        private int delay = PrefUtil.getInt(PrefUtil._3D_LOCK_INTRACTIVE_TIME, 2000);
        private int counter = delay;

        public CountdownTimer() {
            super(UPDATE_RATE, null);
            addActionListener(this);
        }

        @Override
        public void stop() {
            super.stop();
            counter = delay;
        }

        public void actionPerformed(ActionEvent evt) {
            // System.err.println("Full rendering in "+(counter/1000.0)+"s");
            if (counter <= 0) {
                stop();
                return;
            }
            counter -= UPDATE_RATE;
        }
    }

    private static final int UPDATE_RATE = 500;

    private static final double LOWEST_RATE = 0.001;
    private static final double LOW_RATE = 0.01;
    private static final double HIGH_RATE = 5.0;
    private static final double HIGHEST_RATE = 15000;

    protected Timer timer = new DelayTimer();
    protected Timer countdown = new CountdownTimer();

    private Interactor iren;
    private Set<Actor> selection = new HashSet<>();

    private ILookAndFeel laf;
    private VTKMouseHandler handler;
    private VTKPickManager pickManager;

    private CameraManager cameraManager;
    private LowRendering lowRendering = LowRendering.ON;

    public VTKRenderPanel(ILookAndFeel laf) {
        this.laf = laf;
        Initialize();
    }

    private void Initialize() {
        iren = new VTKInteractor(rw);

        double[] color1 = laf != null ? laf.get3DColor1() : VTKColors.BLUE;
        double[] color2 = laf != null ? laf.get3DColor2() : VTKColors.WHITE;
        double[] colorSelection = laf != null ? laf.get3DSelectionColor() : VTKColors.PINK;

        handler = new VTKMouseHandler(this);
        addMouseListener(handler);
        addMouseMotionListener(handler);
        addMouseWheelListener(handler);
        addKeyListener(handler);

        pickManager = new VTKPickManager(this);
        pickManager.pickForActors();

        cameraManager = new VTKCameraManager(this);

        ren.GradientBackgroundOn();
        ren.SetBackground(color1);
        ren.SetBackground2(color2);
        ren.SetGradientBackground(true);

        // ren.RemoveAllLights();
        ren.AutomaticLightCreationOff();
        ren.LightFollowCameraOn();

        ren.AddLight(createLight(45, 45));
        ren.AddLight(createLight(-45, 45));
        ren.AddLight(createLight(45, -45));
        ren.AddLight(createLight(-45, -45));

        sren.AutomaticLightCreationOn();
        sren.GradientBackgroundOff();
        // iren.start();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                updateSize(getWidth(), getHeight());
            }
        });

        // VTKUtil.observe(rw, "");

        // rw.AddObserver("AbortCheckEvent", this, "AbortCheckEvent");
    }

    // public void AbortCheckEvent() {
    // if (rw.GetEventPending() != 0) {
    // System.out.println("VTKRenderPanel.AbortCheckEvent() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
    // rw.SetAbortRender(1);
    // }
    // }

    private vtkLight createLight(double elevation, double azimuth) {
        vtkLight light = new vtkLight();
        light.SetIntensity(0.5);
        light.SetColor(1, 1, 1);
        light.SetLightTypeToCameraLight();
        light.SetDirectionAngle(elevation, azimuth);
        light.SwitchOn();

        return light;
    }

    public void Delete() {
        iren = null;
        super.Delete();
    }

    public void lock() {
        logger.trace("-------------------------- LOCK ---------------------------------");
        Lock();
    }

    public void unlock() {
        logger.trace("------------------------- UNLOCK --------------------------------");
        UnLock();
    }

    @Override
    public void dispose() {
        DestroyTimer();

        iren.dispose();
        iren = null;

        super.dispose();

        Initialize();
    }

    private void updateSize(int w, int h) {
        if (windowset == 1) {
            lock();
            iren.updateSize(w, h);
            unlock();
        }
    }

    public void renderLater() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                Render();
            }
        });
    }

    public void renderAndWait() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Render();
            }
        });
    }

    public void resetZoomLater() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                zoomReset();
            }
        });
    }

    public void resetZoomAndWait() {
        ExecUtil.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                zoomReset();
            }
        });
    }

    @Override
    public void setHighRendering() {
        // System.out.println("VTKRenderPanel.setHighRendering()");
        if (iren == null) {
            return;
        }

        if (lowRendering == LowRendering.OFF) {
            return;
        }

        StartTimer();
    }

    @Override
    public void setLowRendering() {
        // System.out.println("VTKRenderPanel.setLowRendering()");
        if (iren == null) {
            return;
        }

        if (lowRendering == LowRendering.OFF) {
            return;
        }

        DestroyTimer();

        InteractiveOn();
    }

    private void InteractiveOn() {
        // System.err.println("VTKRenderPanel.InteractiveOn()");
        lock();

        for (Actor actor : getAllActors()) {
            actor.interactiveOn();
        }

        unlock();
    }

    private void InteractiveOff() {
        // System.err.println("VTKRenderPanel.InteractiveOff()");
        lock();

        for (Actor actor : getAllActors()) {
            actor.interactiveOff();
        }

        unlock();
        renderLater();
    }

    private void StartTimer() {
        if (timer.isRunning()) {
            timer.stop();
            countdown.stop();
        }

        countdown.start();

        int delay = PrefUtil.getInt(PrefUtil._3D_LOCK_INTRACTIVE_TIME, 2000);
        timer.setDelay(delay);
        timer.setRepeats(false);
        timer.start();
    }

    public void DestroyTimer() {
        if (timer.isRunning()) {
            timer.stop();
            countdown.stop();
        }
    }

    @Override
    public void wheelForward() {
        if (iren == null)
            return;
        lock();
        iren.wheelForwardEvent();
        unlock();
    }

    @Override
    public void wheelBackward() {
        if (iren == null)
            return;
        lock();
        iren.wheelBackwardEvent();
        unlock();
    }

    @Override
    public void zoomReset() {
        if (iren == null)
            return;
        lock();
        GetRenderer().ResetCamera();
        getInteractor().setCenter(getCameraManager().getFocusPoint());
        unlock();
        renderLater();
    }

    @Override
    public void setCameraPosition(Position pos) {
        cameraManager.setCameraPosition(pos);
    }

    @Override
    public void addActor(vtkAssembly actor) {
        lock();
        GetRenderer().AddActor(actor);
        unlock();
    }

    @Override
    public void removeActor(vtkAssembly actor) {
        lock();
        GetRenderer().RemoveActor(actor);
        unlock();
    }

    @Override
    public void addActor(Actor actor) {
        lock();
        actor.setRepresentation(representation);
        GetRenderer().AddActor(actor.getActor());
        correctSelectionVisualization();
        unlock();
    }

    private void correctSelectionVisualization() {
        int memory_limit = PrefUtil.getInt(PrefUtil._3D_TRANSPARENCY_MEMORY, 10 * 1024);
        int size = 0;
        for (Actor actor : getAllActors()) {
            size += actor.getMemorySize();
        }

        logger.trace("Total Memory Size: {}", size);

        for (Actor actor : getAllActors()) {
            if (size > memory_limit) {
                actor.deselectedStateOff();
            } else {
                actor.deselectedStateOn();
            }
        }
    }

    @Override
    public void removeActor(Actor actor) {
        lock();
        GetRenderer().RemoveActor(actor.getActor());
        GetSelectionRenderer().RemoveActor(actor.getSelectionActor());
        correctSelectionVisualization();
        unlock();
    }

    @Override
    public void clearSelection() {
        lock();
        for (Actor actor : getAllActors()) {
            actor.restoreFromSelection();
        }
        unlock();
    }

    @Override
    public void filterActors(Actor... actors) {
        lock();
        List<Actor> toKeep = Arrays.asList(actors);
        List<Actor> allActors = getAllActors();
        for (Actor actor : allActors) {
            if (toKeep.size() > 0) {
                if (toKeep.contains(actor)) {
                    actor.unfilterActor();
                } else {
                    actor.filterActor();
                }
            } else {
                actor.unfilterActor();
            }
        }

        unlock();
        Render();
    }

    @Override
    public void selectActors(boolean keepSelected, Actor... actors) {
        setLowRendering();
        lock();

        if (!keepSelected) {
            selection.clear();
        }

        List<Actor> toSelect = new ArrayList<>();
        if (keepSelected) {
            for (Actor actor : actors) {
                if (selection.contains(actor)) {
                    selection.remove(actor);
                } else {
                    toSelect.add(actor);
                }
            }
        } else {
            toSelect.addAll(Arrays.asList(actors));
        }

        selection.addAll(toSelect);

        for (Actor actor : getAllActors()) {
            // actor.interactiveOn();
            if (selection.size() > 0) {
                if (selection.contains(actor)) {
                    actor.selectActor();
                    GetSelectionRenderer().AddActor(actor.getSelectionActor());
                } else {
                    actor.deselectActor();
                    GetSelectionRenderer().RemoveActor(actor.getSelectionActor());
                }
            } else {
                GetSelectionRenderer().RemoveActor(actor.getSelectionActor());
                actor.restoreFromSelection();
            }
        }
        unlock();
        // renderLater();
        Render();
        // renderAndWait();
        setHighRendering();
    }

    @Override
    public vtkRenderer GetSelectionRenderer() {
        return super.GetSelectionRenderer();
    }

    private List<Actor> getAllActors() {
        List<Actor> list = new ArrayList<>();
        vtkActorCollection actors = ren.GetActors();
        for (int a = 0; a < actors.GetNumberOfItems(); a++) {
            vtkObject item = actors.GetItemAsObject(a);
            if (item instanceof Actor) {
                list.add((Actor) item);
            }
        }

        return list;
    }

    private List<Actor> getSelectionActors() {
        List<Actor> list = new ArrayList<>();
        vtkActorCollection actors = sren.GetActors();
        for (int a = 0; a < actors.GetNumberOfItems(); a++) {
            vtkObject item = actors.GetItemAsObject(a);
            System.out.println("VTKRenderPanel.getSelectionActors() " + item);
        }

        return list;
    }

    @Override
    public void setActorColor(Color c, Actor... actors) {
        if (c == null)
            return;

        lock();
        double[] color = new double[] { c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0 };
        double opacity = c.getAlpha() / 255.0;

        for (Actor actor : actors) {
            if (actor == null)
                continue;
            actor.setSolidColor(color, opacity);

        }
        unlock();
    }

    @Override
    public void clear() {
        selection.clear();
    }

    Representation representation = Representation.SURFACE;

    public void setRepresentation(Representation representation) {
        this.representation = representation;
    }

    public Representation getRepresentation() {
        return representation;
    }

    @Override
    public void changeRepresentation(Representation r) {
        lock();
        setRepresentation(r);
        for (Actor actor : getAllActors()) {
            actor.setRepresentation(representation);
        }
        unlock();
        Render();
    }

    @Override
    public void ParallelProjectionOn() {
        lock();
        GetRenderer().GetActiveCamera().ParallelProjectionOn();
        unlock();
        Render();
    }

    @Override
    public void ParallelProjectionOff() {
        lock();
        GetRenderer().GetActiveCamera().ParallelProjectionOff();
        unlock();
        Render();
    }

    // @Override
    // public vtkRenderer GetRenderer() {
    // return super.GetRenderer();
    // }
    //
    // @Override
    // public vtkRenderWindow GetRenderWindow() {
    // return super.GetRenderWindow();
    // }

    @Override
    public Interactor getInteractor() {
        return this.iren;
    }

    @Override
    public VTKPickManager getPickManager() {
        return pickManager;
    }

    @Override
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public vtkImageData toImageData() {
        vtkWindowToImageFilter w2i = new vtkWindowToImageFilter();
        w2i.SetInput(rw);
        w2i.Modified();
        rw.Render();
        w2i.Update();

        return w2i.GetOutput();
    }

    @Override
    public void lowRenderingOn() {
        this.lowRendering = LowRendering.ON;
    }

    @Override
    public void lowRenderingOff() {
        this.lowRendering = LowRendering.OFF;
    }
}
