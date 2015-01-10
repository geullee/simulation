package kr.geul.simulation;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.BivariateGridInterpolator;

import net.ericaro.surfaceplotter.AbstractSurfaceModel;
import net.ericaro.surfaceplotter.AbstractSurfaceModel.Plotter;
import net.ericaro.surfaceplotter.JSurfacePanel;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotColor;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType;
import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.DuplicateOptionsException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InconsistentOptionException;
import kr.geul.options.exception.InvalidArgumentException;

public class PlotSurface extends Simulation {

	public static void run() throws IOException, InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException, DuplicateOptionsException, 
	InconsistentOptionException {
				
		double[] x = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, y = x;
		double[][] array = new double[10][10];
		
		for (int i = 0; i < 10; i++) {
		
			for (int j = 0; j < 10; j++) {
				
				array[i][j] = i * j;
				
			}
			
		}
		
		plot(x, y, array);
		
	}

	private static void plot(double[] x, double[] y, double[][] array) {
		
		SurfaceModel model = getSurfaceModel(x, y, array);
		JSurfacePanel jsp = new JSurfacePanel(model);
		
        jsp.setTitleText("Hello");
        
        JFrame jf= new JFrame("test");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().add(jsp, BorderLayout.CENTER);
        jf.pack();
        jf.setVisible(true);
		
	}
	
	private static SurfaceModel getSurfaceModel
		(final double[] x, final double[] y, final double[][] array) {
		
		final AbstractSurfaceModel sm = new AbstractSurfaceModel();

		sm.setPlotFunction2(false);
		
		sm.setCalcDivisions(50);
		sm.setDispDivisions(50);
		sm.setContourLines(10);

		sm.setXMin(1);
		sm.setXMax(10);
		sm.setYMin(1);
		sm.setYMax(10);
		
		sm.setBoxed(false);
		sm.setDisplayXY(true);
		sm.setExpectDelay(false);
		sm.setAutoScaleZ(true);
		sm.setDisplayZ(true);
		sm.setMesh(true);
		sm.setPlotType(PlotType.SURFACE);
//		sm.setPlotType(PlotType.WIREFRAME);
//		sm.setPlotType(PlotType.CONTOUR);
//		sm.setPlotType(PlotType.DENSITY);

		sm.setPlotColor(PlotColor.SPECTRUM);
		//sm.setPlotColor(PlotColor.DUALSHADE);
//		sm.setPlotColor(PlotColor.FOG);
		//sm.setPlotColor(PlotColor.OPAQUE);
		
		new Thread(new Runnable() {
			
			BivariateGridInterpolator interpolator = new BicubicSplineInterpolator();
			BivariateFunction surfaceFunction = interpolator.interpolate(x, y, array);
			
			public  float f1( float x, float y)
			{
				return (float) surfaceFunction.value(x, y);
			}
			
			public  float f2( float x, float y)
			{
				return (float) surfaceFunction.value(x, y) + Math.ulp((float) 0.1);
			}
			public void run()
			{
				Plotter p = sm.newPlotter(sm.getCalcDivisions());
				int im=p.getWidth();
				int jm=p.getHeight();
				for(int i=0;i<im;i++)
					for(int j=0;j<jm;j++)
					{
						float x,y;
						x=p.getX(i);
						y=p.getY(j);
						p.setValue(i,j,f1(x,y),f2(x,y) );
					}
			}
		}).start();
		
		return sm;

	}
	
}
