/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.deg.common.profile.constraints.atomic;

import com.scottlogic.deg.common.profile.Field;

import java.util.Objects;

public class IsUniqueConstraint implements AtomicConstraint
{
    public final Field field;

    public IsUniqueConstraint(Field field) {
        this.field = field;
    }

    @Override
    public String toDotLabel() {
        return String.format("%s is unique", field.name);
    }

    public String toString(){
        return String.format(
                "MUST_BE_UNIQUE: %s",
                field.toString());
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        IsUniqueConstraint constraint = (IsUniqueConstraint) o;
        return Objects.equals(field, constraint.field);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field);
    }

}