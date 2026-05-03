package com.hbmspace.client;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ModelCustom {

    // =====================
    // ОБЩИЙ API (безопасен)
    // =====================

    public interface IModel {
        void renderAll();
        void renderOnly(String... parts);
        void renderPart(String part);
        void renderAllExcept(String... excluded);
    }

    // =====================
    // FACTORY
    // =====================

    public static IModel load(String path) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            return ClientLoader.load(path);
        }
        return new DummyModel();
    }

    // =====================
    // SERVER (заглушка)
    // =====================

    private static class DummyModel implements IModel {
        public void renderAll() {}
        public void renderOnly(String... parts) {}
        public void renderPart(String part) {}
        public void renderAllExcept(String... excluded) {}
    }

    // =====================
    // CLIENT ЧАСТЬ
    // =====================

    @net.minecraftforge.fml.relauncher.SideOnly(Side.CLIENT)
    private static class ClientLoader {

        static IModel load(String path) {
            try {
                return new ClientModel(
                    com.hbm.render.loader.AdvancedModelLoader.loadModel(path)
                );
            } catch (Throwable t) {
                return new DummyModel();
            }
        }
    }

    @net.minecraftforge.fml.relauncher.SideOnly(Side.CLIENT)
    private static class ClientModel implements IModel {

        private final com.hbm.render.loader.IModelCustom model;

        ClientModel(com.hbm.render.loader.IModelCustom model) {
            this.model = model;
        }

        public void renderAll() {
            model.renderAll();
        }

        public void renderOnly(String... parts) {
            model.renderOnly(parts);
        }

        public void renderPart(String part) {
            model.renderPart(part);
        }

        public void renderAllExcept(String... excluded) {
            model.renderAllExcept(excluded);
        }
    }
}
