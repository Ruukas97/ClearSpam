package ruukas.clearspam;

import net.minecraft.util.text.ITextComponent;

public class SpamCount {
    private ITextComponent message;
    private int counter = 1;
    private long time;

    public SpamCount(ITextComponent message){
    	this.message = message;
    	this.time = System.currentTimeMillis();
    }
    
    public void increaseCounter(){
    	this.counter++;
    }
    
    public int getCounter(){
    	return this.counter;
    }
    
    public void resetCounter(){
    	this.counter = 1;
    }
    
    public long getTime(){
    	return this.time;
    }
    
    public boolean isSame(ITextComponent mes){
    	return this.message.getUnformattedText().equals(mes.getUnformattedText());
    }

	public void setTime(Long time) {
		this.time = time;
	}
}
