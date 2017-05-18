package com.kii.thing_if.command;

/**
 * Marks a class as a single action.
 * <br>
 * A single action must has the following two information:
 * <ul>
 *     <li>action name identifies the action to be executed.</li>
 *     <li>action value indicates contents of the action to be executed.</li>
 * </ul>
 * A class implement Action interface, we called it a Concrete Action. SDK requires a Concrete Action
 * must has one field to represent the above 2 information. Value of this field to represent action value
 * and name of this field as action name. If you would like to use different name for this field, you can
 * use {@link com.google.gson.annotations.SerializedName} annotation and make value of SerializedName
 * same as action name.
 * <br>
 * <b>Note</b> that SDK doesn't allow use non static inner class as Concrete Action.
 * <br><br>
 * SDK using
 * <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples">Gson </a>
 * to serialize Concrete Action instance or parse json format action object received from server as
 * a Concrete Action instance.
 * <br><br>
 * You must register Concrete Action class to ThingIFAPI instance when constructed by:
 * <ul>
 * <li>{@link com.kii.thing_if.ThingIFAPI.Builder#registerAction(String, String, Class)}.
 * </ul>
 * For example:
 * <pre><code>
 *
 *  // example to use annotation
 *  class TurnPower implements Action {
 *       {@literal @}SerializedName("turnPower") // value of annotation should be action name
 *       public Boolean power;
 *
 *       public Integer anotherField;
 *
 *       public TurnPower(Boolean power,
 *           Integer anotherField) {
 *           this.power = power;
 *           this.anotherField = anotherField;
 *       }
 *  }
 *
 *  // example not to use annotation
 *  class TurnPower2 implements Action {
 *       public Boolean turnPower; // field name should be action name
 *
 *       public Integer anotherField;
 *
 *       public TurnPower(Boolean turnPower,
 *           Integer anotherField) {
 *           this.turnPower = turnPower;
 *           this.anotherField = anotherField;
 *       }
 *  }
 *
 *  // when registering Concrete Action
 *  ThingIFAPI api = ThingIFAPI.Builder
 *      .newBuilder(context, app, owner)
 *      // make sure actionName parameter same as value of SerializedName annotation.
 *      .registerAction("alias1", "turnPower", TurnPower.class);
 *      // make sure actionName parameter same as field name.
 *      .registerAction("alias2", "turnPower", TurnPower2.class)
 *      .build();
 *  </code></pre>
 */
public interface Action {
}
