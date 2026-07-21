package com.example.recipeapp.common.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.example.recipeapp.databinding.BottomSheetDietFilterBinding
import com.example.recipeapp.databinding.ItemChipBinding
import com.example.recipeapp.data.recipes.options.DietOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

// Multi-select diet filter sheet, shared by Home (and eventually Search). Receives the
// caller's currently-applied diets as an argument and reports the new selection back via
// FragmentResult on Apply, so it never needs a ViewModel of its own.
class DietFilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDietFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDietFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedDiets = arguments?.getStringArrayList(ARG_SELECTED_DIETS) ?: emptyList()
        setupDietChips(selectedDiets)
        configureOnClicks()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    // Inflates one item_chip.xml per diet option straight into the ChipGroup. Every inflated
    // chip shares the same android:id from the layout (item_chip.xml only defines one), so
    // this view is never looked up again by id afterwards — see checkedDiets() below.
    private fun setupDietChips(selectedDiets: List<String>) {
        val inflater = LayoutInflater.from(binding.chipGroupDiet.context)
        val selectedSet = selectedDiets.toSet()
        DietOptions.diets.forEach { diet ->
            val chip = ItemChipBinding.inflate(inflater, binding.chipGroupDiet, false).chipItem
            chip.text = diet
            chip.isChecked = diet in selectedSet
            binding.chipGroupDiet.addView(chip)
        }
    }

    private fun configureOnClicks() {
        binding.tvClear.setOnClickListener {
            binding.chipGroupDiet.clearCheck()
            publishResult(emptyList())
            dismiss()
        }
        binding.btnApplyFilter.setOnClickListener {
            publishResult(checkedDiets())
            dismiss()
        }
    }

    // Reads back which diets are checked by walking the ChipGroup's actual children instead
    // of ChipGroup.checkedChipIds + findViewById(id). Every chip here shares one id (see
    // setupDietChips), so an id-based lookup always resolves to the *first* child with that
    // id — collapsing multi-selection down to a single diet regardless of what was actually
    // checked. Iterating `children` directly reads each Chip object's own isChecked state,
    // so duplicate ids can't cause selections to be lost or merged.
    private fun checkedDiets(): List<String> =
        binding.chipGroupDiet.children
            .filterIsInstance<Chip>()
            .filter { it.isChecked }
            .map { it.text.toString() }
            .toList()

    private fun publishResult(selectedDiets: List<String>) {
        setFragmentResult(REQUEST_KEY, bundleOf(RESULT_SELECTED_DIETS to ArrayList(selectedDiets)))
    }

    companion object {
        const val REQUEST_KEY = "diet_filter_request"
        const val RESULT_SELECTED_DIETS = "result_selected_diets"
        private const val ARG_SELECTED_DIETS = "arg_selected_diets"

        // selectedDiets: the caller's current selection, passed in so the sheet opens with
        // the right chips pre-checked instead of always resetting to none.
        fun show(fragmentManager: FragmentManager, selectedDiets: List<String>) {
            DietFilterBottomSheet().apply {
                bundleOf(ARG_SELECTED_DIETS to ArrayList(selectedDiets)).also { arguments = it }
            }.show(fragmentManager, DietFilterBottomSheet::class.simpleName)
        }
    }
}
