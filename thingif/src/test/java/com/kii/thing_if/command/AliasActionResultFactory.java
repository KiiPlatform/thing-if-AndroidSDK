package com.kii.thing_if.command;

import java.util.List;

public class AliasActionResultFactory {
    public static AliasActionResult newAliasActionResult(
            String alias,
            List<ActionResult> results) {
        return new AliasActionResult(alias, results);
    }
}
