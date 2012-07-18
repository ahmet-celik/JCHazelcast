package jchazelcast;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 11.07.2012
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class ItemEvent<I> extends Event {

    public ItemEvent(String type, String name, String structure, boolean includeValue, Object... values) {
        super(type, name, structure, includeValue, values);
    }

    public I getItem(){
        return (I) values[0];
    }

}
