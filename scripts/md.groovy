
System.setProperty("pj.nt","2")

open "examples/trypsin.P1.xyz"

// Compute the initial energy
println energy()
return

// RMS gradient per atom convergence criteria (Kcal/mole/A)
rmsg = 5.0
minimize(rmsg)

// Compute the energy after minimization
println energy()

// Number of molecular dynamics steps
int nSteps = 100
// Time step in femtoseconds.
double timeStep = 1.0
// Frequency to print out thermodynamics information in picoseconds.
double printInterval = 0.01
// Temperature in degrees Kelvin.
double temperature = 300.0
// Reset velocities.
boolean initVelocities = true
// Go!

for (int i = 0; i < 10; i++) {
    md(nSteps, timeStep, printInterval, temperature, initVelocities)
    time()
}
