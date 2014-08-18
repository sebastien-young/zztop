package com.warriormill.warriorengine.drawable;
 
import java.util.ArrayList;
import java.util.Hashtable;
 
import org.xmlpull.v1.XmlPullParser;
 
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
 
/**
* @author maximo guerrero
*/
public class SpriteTile extends Drawable {
private Bitmap tileSheet = null; //sprite tile sheet for all animations. rectangles are used to slip and only show parts of one bitmap
private Hashtable animations = null; //all animation sequences for this sprite
private String currentAnimation="idle"; //current animation sequence
private int currentFrame=0; //current frame being played
private int xpos=0; // x position
private int ypos=0; // y position
private int waitDelay=0; // de lay beforethe next frame
 
private ColorFilter cf = null;
 
// Class contains Information about one frame
private class FrameInfo
{
public Rect rect = new Rect();
public int nextFrameDelay =0;

public FrameInfo(){}
public FrameInfo(Rect r, int nfd){
	rect = r;
	nextFrameDelay = nfd;
}
}
FrameInfo currframe;

//Class encapsulates all the data for an animations sequence. List for frames, animcation name, if the sequence will loop and collission info
private class AnimationSequence
{
public ArrayList sequence;
public Rect collisionRect;
public boolean canLoop =false;
@SuppressWarnings("unused")
public String name="idle";
}

public SpriteTile(int BitmapResourceId, int left, int top, int right, int bottom, Context context){
	loadBitmap(BitmapResourceId, context);
	loadFrame(left, top, right, bottom);
}
 
//takes resource ids for bitmaps and xmlfiles
public SpriteTile(int BitmapResourceId, int XmlAnimationResourceId, Context context){
	loadBitmap(BitmapResourceId, context);
	loadSprite(XmlAnimationResourceId,context);
}

public void loadFrame(int left, int top, int right, int bottom){

	currframe = new FrameInfo(new Rect(left, top, right, bottom), 0);
}
 
//load bitmap and xml data
public void loadBitmap(int spriteid, Context context){
	tileSheet = BitmapFactory.decodeResource(context.getResources(), spriteid);
}

public void loadSprite( int xmlid, Context context) {
	
	//load the xml will all the frame animations into a hashtable
	XmlResourceParser xpp= context.getResources().getXml(xmlid);
 
	animations= new Hashtable();
 
	try{
		int eventType = xpp.getEventType();
		String animationname="";
		AnimationSequence animationsequence = new AnimationSequence();
		while (eventType != XmlPullParser.END_DOCUMENT){
 
			if(eventType == XmlPullParser.START_DOCUMENT) {
				System.out.println("Start document");
 
			} else if(eventType == XmlPullParser.END_DOCUMENT) {
				System.out.println("End document");
 
			} else if(eventType == XmlPullParser.START_TAG) {
				System.out.println("Start tag "+xpp.getName());
				if(xpp.getName().toLowerCase().equals("animation")){
					animationname=xpp.getAttributeValue(null, "name");
					animationsequence = new AnimationSequence();
					animationsequence.name=animationname;
					animationsequence.sequence=new ArrayList();
					animationsequence.canLoop = xpp.getAttributeBooleanValue(null,"canLoop", false);
				}
				else if(xpp.getName().toLowerCase().equals("framerect")){
					FrameInfo frameinfo = new FrameInfo();
					Rect frame = new Rect();
					frame.top = xpp.getAttributeIntValue(null, "top", 0);
					frame.bottom = xpp.getAttributeIntValue(null, "bottom", 0);
					frame.left = xpp.getAttributeIntValue(null, "left", 0);
					frame.right = xpp.getAttributeIntValue(null, "right", 0);
					frameinfo.rect = frame;
					frameinfo.nextFrameDelay = xpp.getAttributeIntValue(null,"delayNextFrame", 0);
					animationsequence.sequence.add(frameinfo);
				}
				else if(xpp.getName().toLowerCase().equals("collisionrect")){
					Rect colrect = new Rect();
					colrect.top = xpp.getAttributeIntValue(null, "top", 0);
					colrect.bottom = xpp.getAttributeIntValue(null, "bottom", 0);
					colrect.left = xpp.getAttributeIntValue(null, "left", 0);
					colrect.right = xpp.getAttributeIntValue(null, "right", 0);
					animationsequence.collisionRect=colrect;
				}
			}else if(eventType == XmlPullParser.END_TAG) {
				if(xpp.getName().toLowerCase().equals("animation")){
					animations.put(animationname, animationsequence);
				}
			}else if(eventType == XmlPullParser.TEXT) {
				System.out.println("Text "+xpp.getText());
 
			}
			eventType = xpp.next();
		}
	}catch (Exception e) {
		Log.e("ERROR", "ERROR IN SPRITE TILE CODE:"+e.toString());
	}
	System.out.println("Sprite Loaded ");
}
//Draw sprite onto screen
@Override
public void draw(Canvas canvas) {
	try{
		if(animations != null){
			AnimationSequence as = (AnimationSequence) animations.get(currentAnimation);
			currframe = (FrameInfo) as.sequence.get(currentFrame);
		}
		Rect rclip = currframe.rect;
		Rect dest = new Rect(this.getXpos(), getYpos(), getXpos() + (rclip.right - rclip.left),
		getYpos() + (rclip.bottom - rclip.top));
		
		if(cf!=null){/*color filter code here*/}
		
		canvas.drawBitmap(tileSheet, rclip, dest, null);
		update(); //after drawing update the frame counter
	}catch (Exception e){
		Log.e("ERROR", "ERROR IN SPRITE TILE CODE:"+e.toString()+e.getStackTrace().toString());
	}
}
 
@Override
public int getOpacity() {
	// TODO Auto-generated method stub
	return 100;
}
 
@Override
public void setAlpha(int alpha) {
	// TODO Auto-generated method stub
 
}
 
@Override
public void setColorFilter(ColorFilter cf) {
	// TODO Auto-generated method stub
	this.cf = cf;
}
 
//updates the frame counter to the next frame
public void update(){
	if(waitDelay==0 && animations != null){
		AnimationSequence as = (AnimationSequence) animations.get(currentAnimation);

		//set current frame back to the first because looping is possible
		if(as.canLoop){
			currentFrame++;
			currentFrame = currentFrame % as.sequence.size();
			//go to next frame
		}
 
		FrameInfo frameinfo= (FrameInfo) as.sequence.get(currentFrame);
		if(frameinfo != null)
			waitDelay = frameinfo.nextFrameDelay; //set delaytime for the next frame
	}else if(waitDelay > 0){
		waitDelay--; //wait for delay to expire
	}
 
}
//has this sprite collided with a rect
public boolean hasCollided(Rect rect)
{
AnimationSequence as = (AnimationSequence) animations.get(currentAnimation);
if( rect.right < as.collisionRect.left )
return false;
if( rect.left > as.collisionRect.right )
return false;
 
if( rect.top > as.collisionRect.bottom )
return false;
if( rect.bottom < as.collisionRect.top )
return false;
 
return true;
}
//has animation finished playing, returns true on a animaiton that can loop for ever
public boolean hasAnimationFinished()
{
AnimationSequence as = (AnimationSequence) animations.get(currentAnimation);
if(as.canLoop || currentFrame == as.sequence.size() -1)
return true;
 
return false;
}
 
public void setXpos(int xpos) {
this.xpos = xpos;
}
 
public int getXpos() {
return xpos;
}
 
public void setYpos(int ypos) {
this.ypos = ypos;
}
 
public int getYpos() {
return ypos;
}

public void addFrame(){
	if(currentFrame + 1 < ((AnimationSequence) animations.get(currentAnimation)).sequence.size()) 
		currentFrame++;
}

public boolean setFrame(int f){
	if(f+1 > 0 && f < ((AnimationSequence) animations.get(currentAnimation)).sequence.size()){
		currentFrame = f;
		return true;
	}
	return false;
}

public int numFrames(){
	return ((AnimationSequence)animations.get(currentAnimation)).sequence.size();
}

public void subFrame(){
	if(currentFrame > 0) currentFrame--;
}

public void setRect(int left, int top, int right,int bottom){
	currframe.rect = new Rect(left, top, right, bottom);
}

}