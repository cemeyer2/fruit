module fruit_basket_gen
  use fruit
contains
  subroutine calculator_test_all_tests
    use calculator_test

    call setup
    write (*, *) "  ..running test: test_calculator_should_produce_4_when_2_and_2_are_inputs"
    call set_unit_name ('test_calculator_should_produce_4_when_2_and_2_are_inputs')
    call test_calculator_should_produce_4_when_2_and_2_are_inputs
    if (.not. is_last_passed()) then
      write(*,*)
      write(*,*) '  Un-satisfied spec:'
      write(*,*) '  -- calculator should produce 4 when 2 and 2 are inputs'
      write(*,*)
    end if
    call teardown

    call setup
    write (*, *) "  ..running test: test_more_with_spec_in_spec_variable"
    call set_unit_name ('test_more_with_spec_in_spec_variable')
    call test_more_with_spec_in_spec_variable
    if (.not. is_last_passed()) then
      write(*,*)
      write(*,*) '  Un-satisfied spec:'
      write(*,*) '  -- calculation should produce 4.0 when 2.0 and 2.0 &
       are &
       inputs'
      write(*,*)
    end if
    call teardown

    call setup
    write (*, *) "  ..running test: test_calculator_should_remember_previous_calculation_results"
    call set_unit_name ('test_calculator_should_remember_previous_calculation_results')
    call test_calculator_should_remember_previous_calculation_results
    if (.not. is_last_passed()) then
      write(*,*)
      write(*,*) '  Un-satisfied spec:'
      write(*,*) '  -- calculator should remember previous calculation results'
      write(*,*)
    end if
    call teardown

  end subroutine calculator_test_all_tests

  subroutine fruit_basket
    call calculator_test_all_tests
  end subroutine fruit_basket

end module fruit_basket_gen
