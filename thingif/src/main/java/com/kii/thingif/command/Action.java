package com.kii.thingif.command;

/**
 * Marks a class as group of actions of command. The class implements Acton defines single actions
 * as fields of the class.
 * <p>The class must define {@link ActionAnnotation} for each field of the class, SDK uses the
 * defined annotations to serialize the class to json data or deserialize json data to an instance
 * of the class. During serialize or deserialize process, SDK treat filed of the class as action of
 * command.
 *
 * <h3>Serialize Principles</h3>
 * <ul>
 *     <li>Name of action after serialized.
 *     <ul>
 *         <li>
 *             If value of actionName of {@link ActionAnnotation} for the field of class is not
 *             an empty string, SDK uses this value as key of action in serialized json object.
 *         </li>
 *         <li>
 *             If value of actionName annotation is empty string, SDK will name of the field
 *             as key of action in serialized json object.
 *         </li>
 *     </ul>
 *     </li>
 *     <li>Value of action after serialized the field.
 *     <ul>
 *         <li>
 *             If value of the field is null, SDK does not serialized this field.
 *         </li>
 *         <li>
 *             If value of the field is not null, SDK serialize this field using
 *             <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples">
 *                 Gson
 *             </a>.
 *         </li>
 *     </ul>
 *     </li>
 * </ul>
 * <h3>Deserialize Steps</h3>
 * <ul>
 *     <li>Find the field of the class to deserialize action.
 *     <ul>
 *         <li>First find the fields defined with {@link ActionAnnotation} and actionName value is
 *         not empty. If one of the fields match, then deserialize the action to this field.
 *         </li>
 *         <li>If no field found in the above step, then find the name of field. If the name is
 *         matched, then deserialize the action to this field.
 *         </li>
 *     </ul>
 *     </li>
 *     <li>Deserialize value of the matched field using
 *         <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples">
 *         Gson
 *         </a>.
 *     <ul>
 *         <li>
 *            If action value is Collection type, the type of acton must be registered in advance.
 *         </li>
 *     </ul>
 *     </li>
 * </ul>
 */
public interface Action {
}
