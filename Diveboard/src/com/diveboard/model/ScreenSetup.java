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
//	static final double[]		_wsp1 = {5.5, 7, 4, 3.25};							// Fragment Whitespace 1
//	static final double[]		_wsp2 = {5, 6,426, 2, 2};							// Fragment Whitespace 2*
//	static final double[]		_wsp3 = {1.5, 4, 3, 3};								// Fragment Whitespace 3*
//	static final double[]		_wsp4 = {1.5, 3, 2, 3};								// Fragment Whitespace 4
//	static final double[]		_frg_bann_h = {7, 12.857, 7, 7};					// Fragment Banner Height
//	static final double[]		_frg_body_h = {65, 57,143, 67, 64};					// Fragment Body Height*
//	static final double[]		_frg_foot_h = {5.7, 12, 0, 0};						// Fragment Footer Height
//	static final double[]		_frg_w = {51.5, 140, 75, 75};						// Fragment Width
//	static final double[]		_frag_out_circ_rad = {38, 80, 60, 60};				// Fragment Outer Circle Radius*
//	static final double[]		_frag_circ_brdr_w = {1.5, 0, 3.2, 3.2};				// Fragment Circle Border Width
//	static final double[]		_frag_body_title = {10, 17,136, 0, 0};				// Fragment Body Title*
//	static final double[]		_frag_pict_circ_rad = {8, 10,71, 0, 4};				// Fragment Picture Circle Radius*
//	static final double[]		_footer = {8, 13, 7.5, 10};							// Fragment Footer
//	static final double[]		_frg_body_wsp1 = {2.5, 0, 0, 0};					// Fragment Body Whitespace 1*
//	static final double[]		_frg_body_wsp2 = {1.3, 3, 0, 0};					// Fragment Body Whitespace 2*
//	static final double[]		_frg_body_wsp3 = {2, 3.57, 0, 0};					// Fragment Body Whitespace 3*
//	static final double[]		_frg_body_wsp4 = {2, 3.57, 0, 0};					// Fragment Body Whitespace 4*
//	static final double[]		_dl_seekbar_h = {4, 6, 0 , 0};						// Dive List Seek Bar Height*
//	static final double[]		_dl_profile_h = {6, 12, 0 , 0};						// Dive List Profile Box Height*
//	static final double[]		_dl_profile_w = {51.5, 70, 0 , 0};					// Dive List Profile Box Width
//	static final double[]		_frag_spict_brdr_margin = {6.5, 6.5, 6.5, 6.5};		// Dive List Small Pictures border margin
	
	// Defines in Percent
	static final double[]		_wsp1 = {5.5, 7, 4, 3.25};							// Fragment Whitespace 1
	static final double[]		_wsp2 = {0, 6,426, 2, 2};							// Fragment Whitespace 2*
	static final double[]		_wsp3 = {0, 4, 3, 3};								// Fragment Whitespace 3*
	static final double[]		_wsp4 = {1.5, 2, 2, 3};								// Fragment Whitespace 4
	static final double[]		_frg_bann_h = {7, 12.857, 7, 7};					// Fragment Banner Height
	static final double[]		_frg_body_h = {65, 57,143, 67, 64};					// Fragment Body Height*
	static final double[]		_frg_foot_h = {5.7, 12, 0, 0};						// Fragment Footer Height
	static final double[]		_frg_w = {51.5, 140, 75, 75};						// Fragment Width
	static final double[]		_frag_out_circ_rad = {38, 80, 60, 60};				// Fragment Outer Circle Radius*
	static final double[]		_frag_circ_brdr_w = {1.5, 0, 3.2, 3.2};				// Fragment Circle Border Width
	static final double[]		_frag_body_title = {10, 17,136, 0, 0};				// Fragment Body Title*
	static final double[]		_frag_pict_circ_rad = {8, 10,71, 0, 4};				// Fragment Picture Circle Radius*
	static final double[]		_footer = {8, 13, 7.5, 10};							// Fragment Footer
	static final double[]		_frg_body_wsp1 = {2.5, 0, 0, 0};					// Fragment Body Whitespace 1*
	static final double[]		_frg_body_wsp2 = {1.3, 3, 0, 0};					// Fragment Body Whitespace 2*
	static final double[]		_frg_body_wsp3 = {2, 3.57, 0, 0};					// Fragment Body Whitespace 3*
	static final double[]		_frg_body_wsp4 = {2, 3.57, 0, 0};					// Fragment Body Whitespace 4*
	static final double[]		_dl_seekbar_h = {5, 8, 0 , 0};						// Dive List Seek Bar Height*
	static final double[]		_dl_profile_h = {6, 12, 0 , 0};						// Dive List Profile Box Height*
	static final double[]		_dl_profile_w = {51.5, 70, 0 , 0};					// Dive List Profile Box Width
	static final double[]		_frag_spict_brdr_margin = {6.5, 6.5, 6.5, 6.5};		// Dive List Small Pictures border margin
	
	
	private int					_screenW;
	private int					_screenH;
	private Mode				_screenMode;
	
	// Pad Dive List
	private int					_diveListWhiteSpace1;
	private int					_diveListWhiteSpace2;
	private int					_diveListWhiteSpace3;
	private int					_diveListWhiteSpace4;
	private int					_diveListFragmentBannerHeight;
	private int					_diveListFragmentWidth;
	private int					_diveListFragmentBodyHeight;
	private int					_diveListFragmentFooterHeight;
	private int					_diveListFragmentHeight;
	private int					_diveListFragmentOutCircleRadius;
	private int					_diveListFragmentCircleBorderWidth;
	private int					_diveListFragmentInCircleRadius;
	private int					_diveListFragmentBodyTitle;
	private int					_diveListFragmentPictureCircleRadius;
	private int					_diveListFooterHeight;
	private int					_diveListFragmentWhitespace1;
	private int					_diveListFragmentWhitespace2;
	private int					_diveListFragmentWhitespace3;
	private int					_diveListFragmentWhitespace4;
	private int					_diveListSeekBarHeight;
	private int					_diveListProfileBoxHeight;
	private int					_diveListProfileBoxWidth;
	private int					_diveListSmallPictureMargin;
	
	public						ScreenSetup(int w, int h)
	{
		setScreenSize(w, h);
	}
	
	public void					setScreenSize(int w, int h)
	{
		_screenW = w;
		_screenH = h;
		_screenMode = (w < h) ? Mode.MOBILE_PORTRAIT : Mode.MOBILE_LANDSCAPE;
		_calculate();
	}
	
	private void				_calculate()
	{
		int mode = _screenMode.ordinal();
		
		_diveListWhiteSpace1 = (int) (_screenH * (_wsp1[mode] / 100));
		_diveListWhiteSpace2 = (int) (_screenH * (_wsp2[mode] / 100));
		_diveListWhiteSpace3 = (int) (_screenH * (_wsp3[mode] / 100));
		_diveListWhiteSpace4 = (int) (_screenH * (_wsp4[mode] / 100));
		_diveListFragmentBannerHeight = (int) (_screenH * (_frg_bann_h[mode] / 100));
		_diveListFragmentBodyHeight = (int) (_screenH * (_frg_body_h[mode] / 100));
		_diveListFragmentFooterHeight = (int) (_screenH * (_frg_foot_h[mode] / 100));
		_diveListFragmentHeight = (int) (_diveListFragmentBannerHeight + _diveListFragmentBodyHeight);
		if (_screenMode == Mode.MOBILE_PORTRAIT || _screenMode == Mode.MOBILE_LANDSCAPE)
			_diveListFragmentWidth = (int) (_screenH / (100 / _frg_w[mode]));
		else
			_diveListFragmentWidth = (int) ((_diveListFragmentBodyHeight + _diveListFragmentBannerHeight) / (100 / _frg_w[mode]));
		if (_screenMode == Mode.MOBILE_PORTRAIT)
		{
			_diveListFragmentOutCircleRadius = (int) (_screenH * (_frag_out_circ_rad[mode] / 100));
			_diveListFragmentCircleBorderWidth = (int) (_screenH * (_frag_circ_brdr_w[mode] / 100));
		}
		else
		{
			_diveListFragmentOutCircleRadius = (int) ((_diveListFragmentBodyHeight + _diveListFragmentBannerHeight) * (_frag_out_circ_rad[mode] / 100));
			_diveListFragmentCircleBorderWidth = (int) ((_diveListFragmentBodyHeight + _diveListFragmentBannerHeight) * (_frag_circ_brdr_w[mode] / 100));
		}
		_diveListFragmentInCircleRadius = (int) (_diveListFragmentOutCircleRadius - _diveListFragmentCircleBorderWidth);
		_diveListFragmentBodyTitle = (int) (_screenH * (_frag_body_title[mode] / 100));
		_diveListFragmentPictureCircleRadius = (int) (_screenH * (_frag_pict_circ_rad[mode] / 100));
		_diveListFooterHeight = (int) (_screenH * (_footer[mode] / 100));
		_diveListFragmentWhitespace1 = (int) (_screenH * (_frg_body_wsp1[mode] / 100));
		_diveListFragmentWhitespace2 = (int) (_screenH * (_frg_body_wsp2[mode] / 100));
		_diveListFragmentWhitespace3 = (int) (_screenH * (_frg_body_wsp3[mode] / 100));
		_diveListFragmentWhitespace4 = (int) (_screenH * (_frg_body_wsp4[mode] / 100));
		_diveListSeekBarHeight = (int) (_screenH * (_dl_seekbar_h[mode] / 100));
		_diveListProfileBoxHeight = (int) (_screenH * (_dl_profile_h[mode] / 100));
		_diveListProfileBoxWidth = (int) (_screenH * (_dl_profile_w[mode] / 100));
		if (_screenMode == Mode.MOBILE_LANDSCAPE)
			_diveListSmallPictureMargin = (int) ((((_diveListFragmentHeight - (_diveListFragmentHeight * (_frag_spict_brdr_margin[mode] / 50))) / 5) - _diveListFragmentPictureCircleRadius) / 2);
		else
			_diveListSmallPictureMargin = (int) ((((_diveListFragmentWidth - (_diveListFragmentWidth * (_frag_spict_brdr_margin[mode] / 50))) / 5) - _diveListFragmentPictureCircleRadius) / 2);
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
	
	public int					getDiveListFragmentFooterHeight() {
		return _diveListFragmentFooterHeight;
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
	
	public int					getDiveListFragmentBodyTitle() {
		return _diveListFragmentBodyTitle;
	}
	
	public int					getDiveListFragmentPictureCircleRadius() {
		return _diveListFragmentPictureCircleRadius;
	}
	
	public int					getDiveListFooterHeight() {
		return _diveListFooterHeight;
	}
	
	public int					getDiveListFragmentWhitespace1() {
		return _diveListFragmentWhitespace1;
	}
	
	public int					getDiveListFragmentWhitespace2() {
		return _diveListFragmentWhitespace2;
	}
	
	public int					getDiveListFragmentWhitespace3() {
		return _diveListFragmentWhitespace3;
	}
	
	public int					getDiveListFragmentWhitespace4() {
		return _diveListFragmentWhitespace4;
	}
	
	public int					getDiveListWhiteSpace4() {
		return _diveListWhiteSpace4;
	}
	
	public int					getDiveListSeekBarHeight() {
		return _diveListSeekBarHeight;
	}
	
	public int					getDiveListProfileBoxHeight() {
		return _diveListProfileBoxHeight;
	}

	public void					setDiveListProfileBoxHeight(int _diveListProfileBoxHeight) {
		this._diveListProfileBoxHeight = _diveListProfileBoxHeight;
	}

	public int					getDiveListProfileBoxWidth() {
		return _diveListProfileBoxWidth;
	}

	public void					setDiveListProfileBoxWidth(int _diveListProfileBoxWidth) {
		this._diveListProfileBoxWidth = _diveListProfileBoxWidth;
	}

	public int					getDiveListSmallPictureMargin() {
		return _diveListSmallPictureMargin;
	}

	public void					setDiveListSmallPictureMargin(int _diveListSmallPictureMargin) {
		this._diveListSmallPictureMargin = _diveListSmallPictureMargin;
	}

	public int					getDiveListWhiteSpace2() {
		return _diveListWhiteSpace2;
	}

	public void					setDiveListWhiteSpace2(int _diveListWhiteSpace2) {
		this._diveListWhiteSpace2 = _diveListWhiteSpace2;
	}

	public int					getDiveListWhiteSpace3() {
		return _diveListWhiteSpace3;
	}

	public void					setDiveListWhiteSpace3(int _diveListWhiteSpace3) {
		this._diveListWhiteSpace3 = _diveListWhiteSpace3;
	}
}
