package com.company;

public class AOHelper {
    int _intvalue;
    String _3addressValue;

    AOHelper(){
        _3addressValue = null;
    }

    AOHelper(int _intvalue, String _3addressValue){
        this._intvalue = _intvalue;
        this._3addressValue = _3addressValue;
    }

    public int get_intvalue() {
        return _intvalue;
    }

    public void set_intvalue(int _intvalue) {
        this._intvalue = _intvalue;
    }

    public String get_3addressValue() {
        return _3addressValue;
    }

    public void set_3addressValue(String _3addressValue) {
        this._3addressValue = _3addressValue;
    }
}
