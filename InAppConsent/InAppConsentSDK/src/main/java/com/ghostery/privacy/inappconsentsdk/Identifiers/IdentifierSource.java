package com.ghostery.privacy.inappconsentsdk.Identifiers;

/**
 * Created by jdonohoo on 6/6/14.
 */

import android.content.Context;

import java.util.List;

/**
 * Produces identifiers
 */
public interface IdentifierSource
{
    /**
     * @return An collection of identifiers this source is able to produce
     */
    List<TypedIdentifier> get(Context context);
}
