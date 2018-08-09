package com.scottlogic.deg.generator.generation.combination_strategies;

import com.scottlogic.deg.generator.generation.databags.DataBag;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FieldExhaustiveCombinationStrategy implements ICombinationStrategy {

    @Override
    public Iterable<DataBag> permute(List<Iterable<DataBag>> dataBagSequences) {
        return new FieldExhaustiveCombinationStrategy.InternalIterable(dataBagSequences);
    }

    class InternalIterable implements Iterable<DataBag> {
        private final List<Iterable<DataBag>> dataRowSequences;

        InternalIterable(List<Iterable<DataBag>> dataRowSequences) {
            this.dataRowSequences = dataRowSequences;
        }

        @Override
        public Iterator<DataBag> iterator() {
            return new FieldExhaustiveCombinationStrategy.InternalIterator(dataRowSequences);
        }
    }

    class InternalIterator implements Iterator<DataBag> {
        private Integer indexOfSequenceToVary;

        private final DataBag[] baselines;
        private final List<Iterable<DataBag>> dataRowSequences;
        private final List<Iterator<DataBag>> sequenceIterators;

        private final boolean isEmpty;

        InternalIterator(List<Iterable<DataBag>> dataRowSequences) {
            this.baselines = new DataBag[dataRowSequences.size()];

            this.sequenceIterators = dataRowSequences.stream()
                .map(Iterable::iterator)
                .collect(Collectors.toList());

            this.indexOfSequenceToVary = null;

            this.dataRowSequences = dataRowSequences;

            for (int i = 0; i < sequenceIterators.size(); i++) {
                if (!sequenceIterators.get(i).hasNext()) {
                    this.isEmpty = true;
                    return;
                }

                this.baselines[i] = sequenceIterators.get(i).next();
            }

            this.isEmpty = false;
        }

        @Override
        public boolean hasNext() {
            if (this.isEmpty)
                return false;

            if (this.indexOfSequenceToVary == null)
                return true; // because this means we haven't output a baselines row yet. I know this code is awful, I'll definitely fix it -MH

            // kind of inefficient
            return this.sequenceIterators.stream().anyMatch(Iterator::hasNext);
        }

        @Override
        public DataBag next() {
            if (this.indexOfSequenceToVary == null) {
                this.indexOfSequenceToVary = 0;

                return Stream.of(this.baselines)
                    .reduce(new DataBag(), (db1, db2) -> DataBag.merge(db1, db2));
            }

            return IntStream.range(0, this.dataRowSequences.size())
                .mapToObj(seqIndex -> {
                    if (seqIndex != this.indexOfSequenceToVary) {
                        return this.baselines[seqIndex];
                    }
                    else {
                        if (!this.sequenceIterators.get(seqIndex).hasNext()) {
                            this.indexOfSequenceToVary++;
                            return this.baselines[seqIndex];
                        }
                        return this.sequenceIterators.get(seqIndex).next();
                    }
                })
                .reduce(new DataBag(), (db1, db2) -> DataBag.merge(db1, db2));
        }
    }
}
