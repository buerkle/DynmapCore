package org.dynmap.hdmap;

import org.dynmap.ConfigurationNode;
import org.dynmap.DynmapCore;
import org.dynmap.hdmap.TexturePackHDShader.ShaderState;
import org.dynmap.utils.BlockStep;
import org.dynmap.utils.LightLevels;
import org.dynmap.utils.MapChunkCache;
import org.dynmap.utils.MapIterator;

public class TexturePackHDCaveShader extends TexturePackHDShader {
    private int maxskylevel;
    private int minemittedlevel;
    
    class CaveShaderState extends TexturePackHDShader.ShaderState {
        private boolean ready;
        private LightLevels ll = new LightLevels();
        
        protected CaveShaderState(MapIterator mapiter, HDMap map, MapChunkCache cache) {
            super(mapiter, map, cache);
        }
        @Override
        public void reset(HDPerspectiveState ps) {
            super.reset(ps);
            ready = false;
        }
        /**
         * Process next ray step - called for each block on route
         * @return true if ray is done, false if ray needs to continue
         */
        public boolean processBlock(HDPerspectiveState ps) {
            if(ready)
                return super.processBlock(ps);
            if((ps.getLastBlockStep() == BlockStep.Y_MINUS) && (ps.getBlockTypeID() == 0)) {  /* In air? */
                ps.getLightLevels(ll);
                if((ll.sky <= maxskylevel) && (ll.emitted > minemittedlevel)) {
                    ready = true;
                }
            }
            return false;
        }
    }
    public TexturePackHDCaveShader(DynmapCore core, ConfigurationNode configuration) {
        super(core, configuration);
        maxskylevel = configuration.getInteger("max-sky-light", 0);
        minemittedlevel = configuration.getInteger("min-emitted-light", 1);
    }
    public HDShaderState getStateInstance(HDMap map, MapChunkCache cache, MapIterator mapiter) {
        return new CaveShaderState(mapiter, map, cache);
    }
}
