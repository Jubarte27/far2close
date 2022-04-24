package tester;

import bktree.MetricDistanceSearchTree;
import bktree.result.Result;

public record SearchResults(double searchTime, MetricDistanceSearchTree<?> tree, Result result) {}
