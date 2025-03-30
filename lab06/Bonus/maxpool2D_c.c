void maxpool2D_c(
    const signed char *input_X,
    int input_X_dimW,
    int input_X_dimH,
    int input_X_dimC,
    signed char *output_Y,
    int kernel_W,
    int kernel_H,
    int stride_W,
    int stride_H
) {
    // Calculate output dimensions
    int output_W = (input_X_dimW - kernel_W) / stride_W + 1;
    int output_H = (input_X_dimH - kernel_H) / stride_H + 1;

    for (int c = 0; c < input_X_dimC; c++) {
        for (int oh = 0; oh < output_H; oh++) {
            for (int ow = 0; ow < output_W; ow++) {
                // Initialize max value to the smallest possible value
                signed char max_val = -128;

                // Apply the kernel to the corresponding region in the input
                for (int kh = 0; kh < kernel_H; kh++) {
                    for (int kw = 0; kw < kernel_W; kw++) {
                        int ih = oh * stride_H + kh;
                        int iw = ow * stride_W + kw;

                        // Calculate the input index
                        int input_idx = c * (input_X_dimW * input_X_dimH) + ih * input_X_dimW + iw;


                        // Update max value
                        if (input_X[input_idx] > max_val) {
                            max_val = input_X[input_idx];
                        }
                    }
                }

                // Calculate the output index and store the result
                int output_idx = c * (output_W * output_H) + oh * output_W + ow;
                output_Y[output_idx] = max_val;
            }
        }
    }
}

