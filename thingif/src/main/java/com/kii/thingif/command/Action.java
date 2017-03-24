package com.kii.thingif.command;

import android.content.Context;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;

import java.util.Map;

/**
 * Marks a class as group of single actions of command. The class must implement this interface and
 * define single actions as fields.
 * SDK uses Action together with alias as {@link AliasAction} in {@link Command#aliasActions},
 * {@link CommandForm#aliasActions}, and {@link TriggeredCommandForm#aliasActions}.
 * <br><br>
 * SDK serializes Action/AliasAction objects using
 * <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples">Gson </a>,
 * when calling the following APIs:
 * <ul>
 * <li>{@link com.kii.thingif.ThingIFAPI#postNewCommand(CommandForm)}
 * <li>{@link com.kii.thingif.ThingIFAPI#postNewTrigger(TriggeredCommandForm, Predicate, TriggerOptions)}
 * </ul>
 * <br>
 * SDK serializes {@link AliasAction} instance as a json object. The key is alias and the value is
 * array of single actions(defined fields in Action class). SDK doesn't include null value of field in serialized
 * json object, so if an Action instance only has 1 non-null field, then the array of single actions
 * only has one element. Because there is not information about the order of fields in Action class,
 * SDK can't not guarantee the order of serialized array of single actions.
 * <pre><code>
 *  class AirConditionerAction implements Action {
 *       {@literal @}SerializedName("turnPower")
 *       public Boolean power;
 *       {@literal @}SerializedName( "setPresetTemperature")
 *       public Integer presetTemperature;
 *
 *       public AirConditionerAction(Boolean power,
 *           Integer presetTemperature) {
 *           this.power = power;
 *           this.presetTemperature = presetTemperature;
 *       }
 *  }
 *  class HumidityAction implements Action {
 *       {@literal @}SerializedName( "setPresetHumidity")
 *       public Integer presetHumidity;
 *
 *       public HumidityAction(Integer presetHumidity) {
 *           this.presetHumidity = presetHumidity;
 *       }
 *  }
 *
 * // action has only 1 non-null field
 *   AliasAction{@code <AirConditionerAction>}action1 =
 *       new AliasAction<>(
 *           "AirConditionerAlias",
 *           new AirConditionerAction(true, null)
 *   );
 *   //serialized json object will be
 *   //{"AirConditionerAlias": [{"turnPower": true}]}
 *
 * // action has 2 non-null fields
 *   AliasAction{@code <AirConditionerAction>} action2 =
 *       new AliasAction<>(
 *           "AirConditionerAlias",
 *           new AirConditionerAction(true, 25)
 *   );
 *   //serialized json object can be
 *   //{"AirConditionerAlias": [{"turnPower": true}, {"setPresetTemperature": 25}]} or
 *   //{"AirConditionerAlias": [{"setPresetTemperature": 25}, {"turnPower": true}]}
 *
 * // action with only 1 field
 *   AliasAction{@code <HumidityAction>} action3 =
 *       new AliasAction<>(
 *           "HumidityAlias",
 *           new HumidityAction(45)
 *   );
 *   // serialized json object will be
 *   // {"HumidityAlias": [{"setPresetHumidity": 45}]}
 * <code/><pre/>
 *
 * SDK serializes aliasActions(list of {@link AliasAction} instance) as array of single actions
 * grouped by alias, the order of aliases is same as the order of {@link AliasAction} instances
 * in the list.
 * <br><br>
 * If the order of actions in command is important, in the aliasActions list, the action of AliasAction
 * instance must have only 1 non-null field.
 * <pre><code>
 *  class AirConditionerAction implements Action {
 *       {@literal @}SerializedName("turnPower")
 *       public Boolean power;
 *       {@literal @}SerializedName( "setPresetTemperature")
 *       public Integer presetTemperature;
 *
 *       public AirConditionerAction(Boolean power,
 *           Integer presetTemperature) {
 *           this.power = power;
 *           this.presetTemperature = presetTemperature;
 *       }
 *  }
 *  class HumidityAction implements Action {
 *       {@literal @}SerializedName( "setPresetHumidity")
 *       public Integer presetHumidity;
 *
 *       public HumidityAction(Integer presetHumidity) {
 *           this.presetHumidity = presetHumidity;
 *       }
 *  }
 *
 *   // when create CommandForm instance to post new command
 *
 * // example of 2 same alias action
 *   AliasAction{@code <AirConditionerAction>} action1 =
 *       new AliasAction<>(
 *           "AirConditionerAlias",
 *           new AirConditionerAction(true, null)
 *   );
 *
 *   AliasAction{@code <AirConditionerAction>} action2 =
 *       new AliasAction<>(
 *           "AirConditionerAlias",
 *           new AirConditionerAction(null, 25)
 *   );
 *
 *   CommandForm form = CommandForm.Builder.newBuilder()
 *       .addAliasAction(action1)
 *       .addAliasAction(action2)
 *       .build();
 *
 *   // serialized actions in request body will be
 *   // [{"AirConditionerAlias": [{"turnPower": true}, {"setPresetTemperature": 25}]}]
 *   // the order of action1 and action2 is guaranteed in the list.
 *
 * // example of 3 diff alias action
 *   AliasAction{@code <AirConditionerAction>} action3 =
 *       new AliasAction<>(
 *           "AirConditionerAlias",
 *           new AirConditionerAction(true, null)
 *   );
 *   AliasAction{@code <HumidityAction>} action4 =
 *       new AliasAction<>(
 *           "HumidityAlias",
 *           new HumidityAction(45)
 *   );
 *
 *   AliasAction{@code <AirConditionerAction>} action5 =
 *       new AliasAction<>(
 *           "AirConditionerAlias",
 *           new AirConditionerAction(null, 25)
 *   );
 *
 *   CommandForm form = CommandForm.Builder.newBuilder()
 *       .addAliasAction(action3)
 *       .addAliasAction(action4)
 *       .addAliasAction(action4)
 *       .build();
 *   // serialized actions in request body will be
 *   // [
 *   //   {"AirConditionerAlias": [{"turnPower": true}]},
 *   //   {"HumidityAlias": [{"setPresetHumidity": 45}]},
 *   //   {"AirConditionerAlias": [{"setPresetTemperature": 25}]}
 *   // ]
 *   // the order of action3, action4 and action5 is guaranteed in the list.
 * <code/></pre>
 *
 * If the order of actions is not important, in the aliasActions list, the action of AliasAction instance
 * can have more than 1 non-null fields.
 * <pre>{@code
 *
 *   // when create CommandForm instance to post new command
 *   AliasAction<AirConditionerAction> action1 =
 *       new AliasAction<>(
 *           "AirConditionerAlias",
 *           new AirConditionerAction(true, 25)
 *   );
 *
 *   AliasAction<HumidityAction> action2 =
 *       new AliasAction<>(
 *           "HumidityAlias",
 *           new HumidityAction(45)
 *   );
 *
 *   CommandForm form = CommandForm.Builder.newBuilder()
 *       .addAliasAction(action1)
 *       .addAliasAction(action2)
 *       .build();
 *
 *   // serialized actions in request body can be
 *   // [
 *   //   {"AirConditionerAlias": [{"turnPower": true}, {"setPresetTemperature": 25}]},
 *   //   {"HumidityAlias": [{"setPresetHumidity", 45}]}
 *   // ]
 *   // or
 *   // [
 *   //   {"AirConditionerAlias": [{"setPresetTemperature": 25}, {"turnPower": true}]},
 *   //   {"HumidityAlias": [{"setPresetHumidity", 45}]}
 *   // ]
 *
 *   // the order of actions is not guaranteed.
 * }</pre>

 * When parsing json formatted action from kii cloud server, SDK uses Gson too. You must register
 * class of Action to ThingIFAPI instance when constructed by the following 2 APIs:
 * <ul>
 * <li>{@link com.kii.thingif.ThingIFAPI.Builder#newBuilder(Context, KiiApp, Owner, Map, Map)}
 * <li>{@link com.kii.thingif.ThingIFAPI.Builder#registerActions(String, Class)}.
 * </ul>
 *
 */
public interface Action {
}
