package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.generation.field_value_sources.*;
import com.scottlogic.deg.generator.restrictions.*;

import java.util.*;

public interface FieldValueSourceEvaluator {
    List<FieldValueSource> getFieldValueSources(FieldSpec fieldSpec);
}