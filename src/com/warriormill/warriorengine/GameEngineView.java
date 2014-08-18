/**
*
*/
package com.warriormill.warriorengine;
//import java.util.concurrent.TimeUnit;
import com.warriormill.warriorengine.drawable.SpriteTile;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
import android.view.View;
import java.util.ArrayList;
import android.graphics.drawable.*;
import android.graphics.BitmapFactory;
 
/**
* @author maximo guerrero
*
*/
public class GameEngineView extends View {
public static ArrayList<Drawable> a;
public static ArrayList<Drawable> b;
public static SpriteTile st;
public static SpriteTile bg;

private final static int S_WIDTH = 540;
private final static int S_HEIGHT = 300;

static int player_xpos = S_WIDTH/2 - 22;
static int player_ypos = S_HEIGHT/2 + 40;
static int[] x, y;
static int dx = 0, dy = 0;
static int player_xvel = 0, player_yvel = 0;
static int sensativity = 20;
static int direction;
 
	GameLoop gameloop;
	private class GameLoop extends Thread{
		private volatile boolean running=true;
		public void run(){
			while(running){
			
				int n_angle = - (int) WarriorEngine.computeAngle();
				if(st.setFrame((int)(((float)(n_angle + sensativity)/(sensativity*2))*st.numFrames())))
					dx = n_angle;

				player_xpos += player_xvel;
				player_ypos += player_yvel;
				st.setXpos(player_xpos);
				st.setYpos(player_ypos);
	
				SpriteTile obstacle;
				for(int i = 0; i < a.size(); i++){
					obstacle = (SpriteTile) a.get(i);
					obstacle.setXpos(x[i] + dx);
					obstacle.setYpos(y[i] + dy);
				}
	
				direction = ((direction + dx) + (S_WIDTH * 3)) % (S_WIDTH * 3);
	
				bg.setRect(direction, 0, direction + S_WIDTH, 300);
	
				postInvalidate();
				pause();
			}
		}
		public void pause(){
			running=false;
		}
		public void start(){
			running=true;
			run();
		}
		public void safeStop(){
			running=false;
			interrupt();
		}
 
	}
	
	public void unload(){
		gameloop.safeStop();
	}
 
public GameEngineView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	// TODO Auto-generated constructor stub
	init(context);
}

public GameEngineView(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
	init(context);
}

public GameEngineView(Context context) {
	super(context);
	// TODO Auto-generated constructor stub
	init(context);
}
 
private void init(Context context){
	direction = 0;
	bg = new SpriteTile( R.drawable.desertbg, direction, 0, direction + 540, 300, context);
	this.setBackground(bg);
	load(0, context);
/*
*/
	gameloop = new GameLoop();
	gameloop.run();
}

@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// TODO Auto-generated method stub
	//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	System.out.println("Width " + widthMeasureSpec);
	setMeasuredDimension(S_WIDTH, S_HEIGHT);
}
 
@Override
protected void onDraw(Canvas canvas) {
	// TODO Auto-generated method stub
	//super.onDraw(canvas);
	Drawable d;
	for(int i = 0; i < b.size(); i++){
		d = b.get(i);
		d.draw(canvas);
	}
	
	for(int i = 0; i < a.size(); i++){
		d = a.get(i);
		d.draw(canvas);
	}
 
	st.draw(canvas);
	gameloop.start();
}

private void load(int scene, Context context){
	a = new ArrayList();
	b = new ArrayList();
	switch(scene){
		case 0:
			//Drawable bg = new BitmapDrawable(context.getResources(), R.drawable.desert_background);
			//b.add(bg);
			x = new int[8];
			y = new int[8];
		default: 
			st = new SpriteTile(R.drawable.cars, R.xml.cars, context);
			break;
	}	
}
 
}
