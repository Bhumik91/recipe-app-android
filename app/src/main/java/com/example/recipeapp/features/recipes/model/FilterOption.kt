package com.example.recipeapp.features.recipes.model

// UI-facing wrapper around a single filter chip (cuisine or diet name) plus whether it is
// currently selected. Kept separate from the raw String lists (CuisineOptions/DietOptions)
// since those are also consumed by the repository layer, which has no concept of selection.
data class FilterOption(
    val label: String,
    val isSelected: Boolean = false
)

// Builds a List<FilterOption> from a plain option list (e.g. CuisineOptions.cuisines) by
// marking every entry present in `selected` as checked. Used whenever a ViewModel needs to
// turn its Set<String> selection into something a chip adapter can render directly.
fun List<String>.toFilterOptions(selected: Set<String> = emptySet()): List<FilterOption> =
    map { FilterOption(label = it, isSelected = it in selected) }
