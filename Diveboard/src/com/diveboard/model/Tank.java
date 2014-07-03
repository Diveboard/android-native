package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class					Tank implements IModel
{
	private Integer				_id;
	private String				_material;
	private String				_gas;
	private Double				_volume;
	private Integer				_multitank;
	private Integer				_o2;
	private Integer				_n2;
	private Integer				_he;
	private Integer				_timeStart;
	private Double				_pStart;
	private Double				_pEnd;
	private String				_volumeUnit;
	private Double				_volumeValue;
	private	String				_pUnit;
	private	Double				_pStartValue;
	private	Double				_pEndValue;
	private JSONObject			_json;
	
	public						Tank(JSONObject json) throws JSONException
	{
		_id = (json.isNull("id")) ? null : json.getInt("id");
		_material = (json.isNull("material")) ? null : json.getString("material");
		_gas = (json.isNull("gas")) ? null : json.getString("gas");
		_volume = (json.isNull("volume")) ? null : json.getDouble("volume");
		_multitank = (json.isNull("multitank")) ? null : json.getInt("multitank");
		_o2 = (json.isNull("o2")) ? null : json.getInt("o2");
		_n2 = (json.isNull("n2")) ? null : json.getInt("n2");
		_he = (json.isNull("he")) ? null : json.getInt("he");
		_timeStart = (json.isNull("time_start")) ? null : json.getInt("time_start");
		_pStart = (json.isNull("p_start")) ? null : json.getDouble("p_start");
		_pEnd = (json.isNull("p_end")) ? null : json.getDouble("p_end");
		_volumeUnit = (json.isNull("volume_unit")) ? null : json.getString("volume_unit");
		_volumeValue = (json.isNull("volume_value")) ? null : json.getDouble("volume_value");
		_pUnit = (json.isNull("p_start_unit")) ? null : json.getString("p_start_unit");
		_pStartValue = (json.isNull("p_start_value")) ? null : json.getDouble("p_start_value");
		_pEndValue = (json.isNull("p_end_value")) ? null : json.getDouble("p_end_value");
		_json = json;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer _id) {
		this._id = _id;
	}

	public String getMaterial() {
		return _material;
	}

	public void setMaterial(String _material) {
		this._material = _material;
	}

	public String getGas() {
		return _gas;
	}

	public void setGas(String _gas) {
		this._gas = _gas;
	}

	public Double getVolume() {
		return _volume;
	}

	public void setVolume(Double _volume) {
		this._volume = _volume;
	}

	public Integer getMultitank() {
		return _multitank;
	}

	public void setMultitank(Integer _multitank) {
		this._multitank = _multitank;
	}

	public Integer getO2() {
		return _o2;
	}

	public void setO2(Integer _o2) {
		this._o2 = _o2;
	}

	public Integer getN2() {
		return _n2;
	}

	public void setN2(Integer _n2) {
		this._n2 = _n2;
	}

	public Integer getHe() {
		return _he;
	}

	public void setHe(Integer _he) {
		this._he = _he;
	}

	public Integer getTimeStart() {
		return _timeStart;
	}

	public void setTimeStart(Integer _timeStart) {
		this._timeStart = _timeStart;
	}

	public Double getPStart() {
		return _pStart;
	}

	public void setPStart(Double _pStart) {
		this._pStart = _pStart;
	}

	public Double getPEnd() {
		return _pEnd;
	}

	public void setPEnd(Double _pEnd) {
		this._pEnd = _pEnd;
	}

	public String getVolumeUnit() {
		return _volumeUnit;
	}

	public void setVolumeUnit(String _volumeUnit) {
		this._volumeUnit = _volumeUnit;
	}

	public Double getVolumeValue() {
		return _volumeValue;
	}

	public void setVolumeValue(Double _volumeValue) {
		this._volumeValue = _volumeValue;
	}

	public String getPUnit() {
		return _pUnit;
	}

	public void setPUnit(String _pUnit) {
		this._pUnit = _pUnit;
	}

	public Double getPStartValue() {
		return _pStartValue;
	}

	public void setPStartValue(Double _pStartValue) {
		this._pStartValue = _pStartValue;
	}

	public Double getPEndValue() {
		return _pEndValue;
	}

	public void setPEndValue(Double _pEndValue) {
		this._pEndValue = _pEndValue;
	}
	
	public JSONObject getJson() {
		return _json;
	}

	@Override
	public ArrayList<Pair<String, String>> getEditList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearEditList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyEdit(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		
	}
}
