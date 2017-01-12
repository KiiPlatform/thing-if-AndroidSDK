package com.kii.thingif.command;

/**
 * Marks a class as group of actions of command. The class implements Acton defines single actions
 * as fields of the class.
 * <p>The class must define {@link ActionAnnotation} for each field of the class, SDK uses the
 * defined annotations to serialize the class to json data or deserialize json data to a instance
 * of the class.
 *
 * <h3>Serialize Principles</h3>
 * <ul>
 *     <li>Name of action after serialized.
 *     <ul>
 *         <li>
 *             If value of actionName of {@link ActionAnnotation} is not an empty string, SDK uses
 *             this value as key of action in serialized json object.
 *         </li>
 *         <li>
 *             If value of actionName annotation is empty string, SDK will name of attribute of
 *             the class as key of action in serialized json object.
 *         </li>
 *     </ul>
 *     </li>
 *     <li>Value of action is serialized with Gson.
 *     <ul>
 *         <li>
 *             If action value is null, SDK does not include the action into serialized json data.
 *         </li>
 *         <li>
 *             If action value is primitive type(like int, boolean, float, double), SDK serializes
 *             it by its type.
 *         </li>
 *         <li>
 *             If action value is simple data type(like String, Number or subclass of Number(Integer,
 *             Float, etc.)), SDK serializes it by its type.
 *         </li>
 *         <li>
 *             If action value is customized object defined by developer, SDK serializes it using Gson.
 *         </li>
 *         <li>
 *            If action value is Collection type, SDK serializes it using Gson.
 *         </li>
 *         <li>
 *            If action value is array(like int[]), SDK serialized it using Gson.
 *         </li>
 *     </ul>
 *     </li>
 * </ul>
 */
public interface Action {
}
