package com.wwwjf.videodemo.camera;

import com.wwwjf.videodemo.camera.filter.MagicAntiqueFilter;
import com.wwwjf.videodemo.camera.filter.MagicBrannanFilter;
import com.wwwjf.videodemo.camera.filter.MagicCoolFilter;
import com.wwwjf.videodemo.camera.filter.MagicFreudFilter;
import com.wwwjf.videodemo.camera.filter.MagicHefeFilter;
import com.wwwjf.videodemo.camera.filter.MagicHudsonFilter;
import com.wwwjf.videodemo.camera.filter.MagicInkwellFilter;
import com.wwwjf.videodemo.camera.filter.MagicN1977Filter;
import com.wwwjf.videodemo.camera.filter.MagicNashvilleFilter;
import com.wwwjf.videodemo.camera.basefilter.GPUImageFilter;

public class MagicFilterFactory {

    private static MagicFilterType filterType = MagicFilterType.NONE;

    public static GPUImageFilter initFilters(MagicFilterType type) {
        if (type == null) {
            return null;
        }
        filterType = type;
        switch (type) {
            case ANTIQUE:
                return new MagicAntiqueFilter();
            case BRANNAN:
                return new MagicBrannanFilter();
            case FREUD:
                return new MagicFreudFilter();
            case HEFE:
                return new MagicHefeFilter();
            case HUDSON:
                return new MagicHudsonFilter();
            case INKWELL:
                return new MagicInkwellFilter();
            case N1977:
                return new MagicN1977Filter();
            case NASHVILLE:
                return new MagicNashvilleFilter();
            case COOL:
                return new MagicCoolFilter();
            case WARM:
                return new MagicWarmFilter();
            default:
                return null;
        }
    }

    public MagicFilterType getCurrentFilterType() {
        return filterType;
    }

    private static class MagicWarmFilter extends GPUImageFilter {
    }
}