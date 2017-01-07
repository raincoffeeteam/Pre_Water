package cn.sfw.zju.common;

public enum ResponseCode {
	SUCCESS,
	REQUEST_FAIL,
	FAIL
	;

	private ResponseCode(){
	}

	public static ResponseCode valueOf(int ordinal){
		if (ordinal < 0 || ordinal >= values().length) {  
            throw new IndexOutOfBoundsException("Invalid ordinal");  
        }  
        return values()[ordinal];  
    }  

}
