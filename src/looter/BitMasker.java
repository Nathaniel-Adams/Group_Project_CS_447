// ======================================================
// @file BitMasker.java 
// @author: Ramsay Fencl
// Documented by: Nathaniel Adams
// @description: Bit masker is a helper function to tell
//  what a block is such as the collisions and height.
//  i.e texture height collisions
// ======================================================

//	===============
	package looter;
//	===============

//	========================
	public class BitMasker {
		// & to retrieve || to set
		// 1,3,7,15,31,63,127,255
		
		// | E-indx | H/O/T/D | Interactive | Active | T-index | F/W | Tileset | Light| min_h | max_h |exist?|
		// |  000  |    00   |      0      |   0    |  00000  |  0  |   000   | 0000 | 00000 | 00000 |  0   |
		// shifts: 0,1,6,11,15,18,19,24,25,26,28
		// Total Bits = 31/32  // Last bit is saved for signbit
		
		// max_h: where the block ends
		// min_h: where the block begins
		// Light: determines the (inverse) light value of the specified block 0 to -15, a value of 15 will be black and 0 will be fully lit
		// Tileset: determines which tileset to grab from image path
		// F/W: determines whether to grab the T-Index from the floor or wall arrays within the tileset
		// T-Index: index within the specified set the texture is stored within the imagemap class
		// Interactive: flag to determine if player prompt will appear when within the block, effect depends on the HOTD and E-Index
		// HOTD: Hazard/Objective/Toggle/Door == 0,1,2,3 respectively
		// E-Index: effects are based off of HOTD:
		// 		Hazards, Objectives, and Toggles use this like an ID to tie the tile to the desired effect within their arrays (see Room class)
		//		Door can be used to mark where adjacent rooms need to be attached with the values 0-3 for the directions up/down/left/right respectively
		//		Door can also be used to designate spawners for any value above 3, 7 being the value to declare where the player will spawn for spawn rooms.
		
//		bit overwrite method, alters bits within the specified mask only
//		num: Current bits
//		mask: The specific bits you want to change
// 		input: What ever value you are setting in the mask area
//		===============================================================
		public static int overwriteBits(int num, int mask, int input) {
			if (input < 0) 
				return (num&(~mask)) | 0;
			else if (input >= mask) 
				return (num&(~mask)) | mask;
			else 
				return (num&(~mask)) | input;
		} // overwriteBites()
//		=====================
		
		
// 		gets exist bit
//		num: the bits
//		======================================
		public static int getExists(int num) {
			num &= 1;
			return num;
		}// getExists()
//		===============
		
// 		sets exist bit
// 		num: the bits
//		======================================
		public static int setExists(int num) {
			num ^= 1;
			return num;
		}// setExists()
//		===============
		
// 		gets the MAX height of the value, used to define the top of objects
//		num: the bits
//		======================================================================
		public static int getMaxHeight(int num) {
			if (getExists(num) == 0) 
				return -1;
			return (num>>1)&31;
		}// getMaxHeight()
//		==================
		
// 		gets the MIN height of the value, used to define the bottom of objects
//		num: the bits
//		=========================================
		public static int getMinHeight(int num) {
			return (num>>6)&31;
		}// getMinHeight()
//		==================
		
//	 	Set height of the block
//		num: the bits
//		min: Min bit value you want
//		max: Max bit value you want
//		=========================================================
		public static int setHeights(int num, int min, int max) {
			int ret = setMaxHeight(num, max);
			return setMinHeight(ret, min);
		}// setHeights()
//		================
		
// 		sets the "floor" height as a fraction of 4
//		num: the bits
//		value: The value you would want for the height.
//		====================================================
		public static int setMaxHeight(int num, int value) {
			return overwriteBits(num, 31<<1, value<<1);
		}// setMaxHeight()
//		==================
		
// 		Set how small
//		num: the bits
//		value: value of the min height you would like
		public static int setMinHeight(int num, int value) {
			return overwriteBits(num, 31<<6, value<<6);
		}// setMinHeight
//		================
		
//		Get the light values
//		num: the bits
//		=====================================
		public static int getLight(int num) {
			return (num >> 11) & 15;
		}// getLight()
//		==============
		
//		Set the light value
//		num: the bits
//		value: The value of the light
//		================================================
		public static int setLight(int num, int value) {
			return overwriteBits(num, 15<<11, value<<11);
		}// setLight()
//		==============
		
//		Get the tile set value
//		num: the bits
//		=======================================
		public static int getTileSet(int num) {
			return (num>>15) & 7;
		}// getTileSet()
//		================
		
//		Set the tile set value
//		num: the bits
//		value: Set the tile set
//		==================================================
		public static int setTileSet(int num, int value) {
			return overwriteBits(num, 7<<15, value<<15);
		}// setTileSet()
//		===============
		
//		Get floor or wall
//		num: The bits
//		==================================
		public static int getFW(int num) {
			return (num >> 18) & 1;
		}// getFw()
//		===========
		
//		Set the bit if it's a floor or wall
//		num: the bits
//		value: The new value you want to set
//		=============================================
		public static int setFW(int num, int value) {
			return overwriteBits(num, 1<<18, value<<18);
		}// setFW()
//		===========

//		Get the Texture Index
//		num: the bits
//		============================================
		public static int getTextureIndex(int num) {
			return (num >> 19) & 31;
		}// getTextureIndex()
//		=====================
	
//		Set the texture Index
//		num: the bits
//		value: The new value you want to set
//		=======================================================	
		public static int setTextureIndex(int num, int value) {
			return overwriteBits(num, 31<<19, value<<19);
		}// setTextureIndex()
//		=====================
		
//		Get the active bit
//		num: the bits
//		value: The new value you want to set
//		=====================================-
		public static int getActive(int num) {
			return (num >> 24) & 1;
		}// getActive()
//		===============
		
//		Set the active bit
//		num: the bits
//		value: The new value you want to set
//		=================================================
		public static int setActive(int num, int value) {
			return overwriteBits(num, 1<<24, value<<24);
		}// setActive()
//		===============
		
//		Get the Interactable bit
//		num: the bits
//		============================================
		public static int getInteractable(int num) {
			return (num>>25) & 1;
		}// getInteractable
//		===================
		
//		Set Interactable bit
//		num: the bits
//		value: The new value you want to set
//		=======================================================
		public static int setInteractable(int num, int value) {
			return overwriteBits(num, 1<<25, value<<25);
		}// setInteractable()
//		=====================

//		Get hot bit
//		num: the bits
//		===================================
		public static int getHOT(int num) {
			return (num>>26) & 3;
		}// getHOT()
//		===========
		
//		Set the hot bit
//		num: the bits
//		value: The new value you want to set
//		==============================================
		public static int setHOT(int num, int value) {
			return overwriteBits(num, 3<<26, value<<26);
		}// setHOT()
//		============
		
//		Get the effect Index
//		num: the bits
//		===========================================
		public static int getEffectIndex(int num) {
			return (num>>28) & 7;
		}// getEffectIndex()
//		====================
		
//		Set the effect Index
//		num: the bits
//		value: The new value you want to set
//		======================================================
		public static int setEffectIndex(int num, int value) {
			return overwriteBits(num, 7<<28, value<<28);
		}// setEffectIndex()
//		====================
		
	}//class BitMasker()
//	====================
