package yerova.botanicpledge.client.render.items;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import yerova.botanicpledge.common.items.block_items.ManaYggdralBufferBlockItem;

public class ManaYggdralBufferBlockItemRenderer extends GeoItemRenderer<ManaYggdralBufferBlockItem> {


    public ManaYggdralBufferBlockItemRenderer() {
        super(new ManaYggdralBufferBlockItemModel());
    }
}
