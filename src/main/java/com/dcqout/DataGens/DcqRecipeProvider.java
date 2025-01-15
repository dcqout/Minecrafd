package com.dcqout.DataGens;

import com.dcqout.Main.registrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class DcqRecipeProvider extends RecipeProvider {
    public DcqRecipeProvider(HolderLookup.Provider p_364298_, RecipeOutput p_361287_) {
        super(p_364298_, p_361287_);
    }

    @Override
    protected void buildRecipes() {
        //shaped
        this.shaped(RecipeCategory.COMBAT, registrator.Items.golden_hammer.get())
                .define('#', Items.STICK).define('Z',Items.RESIN_BRICK).define('X', ItemTags.GOLD_TOOL_MATERIALS)
                .pattern("XZX")
                .pattern("XZX")
                .pattern(" # ")
                .unlockedBy("has_gold_ingot", this.has(ItemTags.GOLD_TOOL_MATERIALS))
                .unlockedBy("has_resin_brick", this.has(Items.RESIN_BRICK)).save(this.output);
        this.shaped(RecipeCategory.COMBAT, registrator.Items.diamond_hammer.get())
                .define('#', Items.STICK).define('Z',Items.RESIN_BRICK).define('X', ItemTags.DIAMOND_TOOL_MATERIALS)
                .pattern("XZX")
                .pattern("XZX")
                .pattern(" # ")
                .unlockedBy("has_diamond", this.has(ItemTags.DIAMOND_TOOL_MATERIALS))
                .unlockedBy("has_resin_brick", this.has(Items.RESIN_BRICK)).save(this.output);
        this.shaped(RecipeCategory.COMBAT, registrator.Items.s_wooden_sword.get())
                .define('#', Items.STICK).define('X', ItemTags.WOODEN_TOOL_MATERIALS)
                .pattern("  X")
                .pattern(" X ")
                .pattern("#  ")
                .unlockedBy("has_stick", this.has(Items.STICK)).save(this.output);
        this.shaped(RecipeCategory.COMBAT, registrator.Items.s_stone_sword.get())
                .define('#', Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS)
                .pattern("  X")
                .pattern(" X ")
                .pattern("#  ")
                .unlockedBy("has_cobblestone", this.has(ItemTags.STONE_TOOL_MATERIALS)).save(this.output);
        this.shaped(RecipeCategory.COMBAT, registrator.Items.s_golden_sword.get())
                .define('#', Items.STICK).define('X', ItemTags.GOLD_TOOL_MATERIALS)
                .pattern("  X")
                .pattern(" X ")
                .pattern("#  ")
                .unlockedBy("has_gold_ingot", this.has(ItemTags.GOLD_TOOL_MATERIALS)).save(this.output);
        this.shaped(RecipeCategory.COMBAT, registrator.Items.s_iron_sword.get())
                .define('#', Items.STICK).define('X', ItemTags.IRON_TOOL_MATERIALS)
                .pattern("  X")
                .pattern(" X ")
                .pattern("#  ")
                .unlockedBy("has_iron_ingot", this.has(ItemTags.IRON_TOOL_MATERIALS)).save(this.output);
        this.shaped(RecipeCategory.COMBAT, registrator.Items.s_diamond_sword.get())
                .define('#', Items.STICK).define('X', ItemTags.DIAMOND_TOOL_MATERIALS)
                .pattern("  X")
                .pattern(" X ")
                .pattern("#  ")
                .unlockedBy("has_diamond", this.has(ItemTags.DIAMOND_TOOL_MATERIALS)).save(this.output);
        //cooking
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(registrator.Items.golden_hammer.get(), registrator.Items.s_golden_sword.get()),
                        RecipeCategory.MISC, Items.GOLD_NUGGET, 0.1F, 200)
                .unlockedBy("has_golden_hammer", this.has(registrator.Items.golden_hammer.get()))
                .unlockedBy("has_golden_short_sword", this.has(registrator.Items.s_golden_sword.get()))
                .save(this.output, getSmeltingRecipeName(Items.GOLD_NUGGET));
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(registrator.Items.s_iron_sword.get()),
                        RecipeCategory.MISC, Items.IRON_NUGGET, 0.1F, 200)
                .unlockedBy("has_iron_short_sword", this.has(registrator.Items.s_iron_sword.get()))
                .save(this.output, getSmeltingRecipeName(Items.IRON_NUGGET));

        //netheriteSmithing
        this.netheriteSmithing(registrator.Items.diamond_hammer.get(),RecipeCategory.TOOLS,registrator.Items.netherite_hammer.get());
        this.netheriteSmithing(registrator.Items.s_diamond_sword.get(),RecipeCategory.TOOLS,registrator.Items.s_netherite_sword.get());
    }

    public static class DcqRunner extends RecipeProvider.Runner {
        public DcqRunner(PackOutput p_365442_, CompletableFuture<HolderLookup.Provider> p_362168_) {
            super(p_365442_, p_362168_);
        }

        protected RecipeProvider createRecipeProvider(HolderLookup.Provider p_364945_, RecipeOutput p_362956_) {
            return new DcqRecipeProvider(p_364945_, p_362956_);
        }

        public String getName() {
            return "Dcq Recipes";
        }
    }
}
