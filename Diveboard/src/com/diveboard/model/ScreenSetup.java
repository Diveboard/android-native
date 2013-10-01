package com.diveboard.model;

public class					ScreenSetup
{
	public enum					Mode
	{
		MOBILE_PORTRAIT,
		MOBILE_LANDSCAPE,
		PAD_PORTRAIT,
		PAD_LANDSCAPE
	}
	
	// Defines in Percent
	static final double[]		_wsp1 = {0, 0, 4, 3.25};				// Fragment Whitespace 1
	static final double[]		_wsp2 = {0, 0, 2, 2};					// Fragment Whitespace 2
	static final double[]		_wsp3 = {0, 0, 3, 3};					// Fragment Whitespace 3
	static final double[]		_wsp4 = {0, 0, 2, 3};					// Fragment Whitespace 4
	static final double[]		_frg_bann_h = {0, 0, 7, 7};				// Fragment Banner Height
	static final double[]		_frg_body_h = {0, 0, 67, 64};			// Fragment Body Height
	static final double[]		_frg_w = {0, 0, 75, 75};				// Fragment Width
	static final double[]		_frag_out_circ_rad = {0, 0, 60, 60};	// Fragment Outer Circle Radius
	static final double[]		_frag_circ_brdr_w = {0, 0, 3.2, 3.2};	// Fragment Circle Border Width
	static final double[]		_footer = {0, 0, 7.5, 10};				// Fragment Footer
	
	
	private int					_screenW;
	private int					_screenH;
	private Mode				_screenMode;
	
	// Pad Dive List
	private int					_diveListWhiteSpace1;
	private int					_diveListFragmentBannerHeight;
	private int					_diveListFragmentWidth;
	private int					_diveListFragmentBodyHeight;
	private int					_diveListFragmentHeight;
	private int					_diveListFragmentOutCircleRadius;
	private int					_diveListFragmentCircleBorderWidth;
	private int					_diveListFragmentInCircleRadius;
	private int					_diveListFooterHeight;
	
	public						ScreenSetup(int w, int h)
	{
		setScreenSize(w, h);
	}
	
	public void					setScreenSize(int w, int h)
	{
		_screenW = w;
		_screenH = h;
		_screenMode = (w < h) ? Mode.PAD_PORTRAIT : Mode.PAD_LANDSCAPE;
		_calculate();
	}
	
	private void				_calculate()
	{
		int mode = _screenMode.ordinal();
		
		_diveListWhiteSpace1 = (int) (_screenH * (_wsp1[mode] / 100));
		_diveListFragmentBannerHeight = (int) (_screenH * (_frg_bann_h[mode] / 100));
		_diveListFragmentBodyHeight = (int) (_screenH * (_frg_body_h[mode] / 100));
		_diveListFragmentHeight = (int) (_diveListFragmentBannerHeight + _diveListFragmentBodyHeight);
		_diveListFragmentWidth = (int) ((_diveListFragmentBodyHeight + _diveListFragmentBannerHeight) / (100 / _frg_w[mode]));
		_diveListFragmentOutCircleRadius = (int) ((_diveListFragmentBodyHeight + _diveListFragmentBannerHeight) * (_frag_out_circ_rad[mode] / 100));
		_diveListFragmentCircleBorderWidth = (int) ((_diveListFragmentBodyHeight + _diveListFragmentBannerHeight) * (_frag_circ_brdr_w[mode] / 100));
		_diveListFragmentInCircleRadius = (int) (_diveListFragmentOutCircleRadius - _diveListFragmentCircleBorderWidth);
		_diveListFooterHeight = (int) (_screenH * (_footer[mode] / 100));
		
	}
	
	public int					getScreenHeight() {
		return _screenH;
	}
	
	public int					getScreenWidth() {
		return _screenW;
	}
	
	public int					getDiveListWhiteSpace1() {
		return _diveListWhiteSpace1;
	}
	
	public int					getDiveListFragmentBannerHeight() {
		return _diveListFragmentBannerHeight;
	}
	
	public int					getDiveListFragmentBodyHeight() {
		return _diveListFragmentBodyHeight;
	}
	
	public int					getDiveListFragmentWidth() {
		return _diveListFragmentWidth;
	}
	
	public int					getDiveListFragmentHeight() {
		return _diveListFragmentHeight;
	}
	
	public int					getDiveListFragmentOutCircleRadius() {
		return _diveListFragmentOutCircleRadius;
	}
	
	public int					getDiveListFragmentCircleBorderWidth() {
		return _diveListFragmentCircleBorderWidth;
	}
	
	public int					getDiveListFragmentInCircleRadius() {
		return _diveListFragmentInCircleRadius;
	}
	
	public int					getDiveListFooterHeight() {
		return _diveListFooterHeight;
	}
}
